package com.example.geolearn.data;

import android.util.Log;

import com.example.geolearn.model.AiMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Repository gọi trực tiếp Google Gemini API (streamGenerateContent, SSE).
 * Mỗi dòng: data: {"candidates":[{"content":{"parts":[{"text":"token"}]}}]}
 *
 * Lấy API Key tại: https://aistudio.google.com/apikey
 */
public class GeminiRepository {

    private static final String TAG = "GeminiRepository";

    // ── Cấu hình Gemini ───────────────────────────────────────────────
    // Đặt GEMINI_API_KEY=... trong local.properties (không commit)
    private static final String API_KEY = com.example.geolearn.BuildConfig.GEMINI_API_KEY;
    // gemini-2.5-flash đã đóng với key mới (404); 3.5-flash là bản chạy được khi thử 2026-11-07
    private static final String MODEL = "gemini-3.5-flash";
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":streamGenerateContent?alt=sse";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // ── System Prompt ─────────────────────────────────────────────────
    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý học tập môn Địa lý thông minh của ứng dụng GeoLearn. " +
            "Hãy trả lời bằng tiếng Việt ngắn gọn, súc tích, đi thẳng vào vấn đề, không giải thích dài dòng lan man, không đề xuất phương pháp học tập. " +
            "Nhiệm vụ chính của bạn là giải đáp các câu hỏi về Địa lý tự nhiên, Địa lý dân cư - kinh tế, bản đồ và Atlat, Địa lý thế giới, đồng thời chấm điểm và nhận xét bài tập của sinh viên. " +
            "Hãy thân thiện chào hỏi và tương tác cơ bản khi bắt đầu cuộc trò chuyện. " +
            "Khi giải thích: nêu đúng số liệu, tên địa danh, và giải thích siêu ngắn (1-2 câu). Với bài tập tính toán (tỉ lệ bản đồ, mật độ dân số...), trình bày ngắn gọn các bước tính. " +
            "Hạn chế danh sách gạch đầu dòng phức tạp, trả lời gọn gàng và tối giản nhất. " +
            "Hãy luôn hỗ trợ nhiệt tình, KHÔNG từ chối các câu hỏi liên quan đến Địa lý, học tập hoặc tương tác thông thường.";

    // ── Callback Interface ────────────────────────────────────────────
    public interface StreamCallback {
        void onToken(String token);
        void onComplete();
        void onError(String errorMessage);
    }

    // ── OkHttp Client ─────────────────────────────────────────────────
    private final OkHttpClient client;
    private Call currentCall;

    public GeminiRepository() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS) // Gemini có lúc >50s mới trả token đầu
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    // ── Public API ────────────────────────────────────────────────────

    public void streamMessage(List<AiMessage> history, StreamCallback callback) {
        cancelCurrentCall();

        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            callback.onError("Trợ lý AI chưa được cấu hình API key. Vui lòng liên hệ quản trị viên.");
            return;
        }

        String requestBodyJson = buildRequestBody(history);
        Log.d(TAG, "Sending to Gemini: " + requestBodyJson);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(RequestBody.create(requestBodyJson, JSON))
                .addHeader("x-goog-api-key", API_KEY)
                .build();

        currentCall = client.newCall(request);
        currentCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    Log.d(TAG, "Request was canceled");
                    return;
                }
                Log.e(TAG, "Network failure / timeout", e);
                callback.onError("Không thể kết nối đến server. Vui lòng kiểm tra mạng.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: HTTP " + response.code() + " " + response.message());
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    Log.e(TAG, "API error " + response.code() + ": " + errorBody);
                    callback.onError(parseApiError(response.code()));
                    return;
                }
                parseStreamingResponse(response, callback);
            }
        });
    }

    public void cancelCurrentCall() {
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
    }

    // ── Private Helpers ───────────────────────────────────────────────

    /** Đọc SSE: mỗi dòng "data: {...}" là một chunk; stream kết thúc khi server đóng kết nối. */
    private void parseStreamingResponse(Response response, StreamCallback callback) {
        ResponseBody body = response.body();
        if (body == null) {
            callback.onError("Phản hồi trống từ server.");
            return;
        }

        try (InputStream inputStream = body.byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data: "))
                    continue;

                String token = extractTokenFromChunk(line.substring(6).trim());
                if (token != null && !token.isEmpty()) {
                    callback.onToken(token);
                }
            }

            callback.onComplete();

        } catch (IOException e) {
            if (currentCall != null && currentCall.isCanceled())
                return;
            Log.e(TAG, "Stream read error", e);
            callback.onError("Lỗi đọc phản hồi từ server.");
        }
    }

    /** Ghép text của mọi part trong candidates[0].content.parts. */
    private String extractTokenFromChunk(String jsonStr) {
        try {
            JSONArray candidates = new JSONObject(jsonStr).optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0)
                return null;

            JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
            JSONArray parts = content == null ? null : content.optJSONArray("parts");
            if (parts == null)
                return null;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                sb.append(parts.getJSONObject(i).optString("text", ""));
            }
            return sb.toString();

        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse chunk: " + jsonStr);
            return null;
        }
    }

    /**
     * Body theo format Gemini generateContent:
     * {"systemInstruction":{"parts":[{"text":"..."}]},
     *  "contents":[{"role":"user"|"model","parts":[{"text":"..."}]}]}
     */
    private String buildRequestBody(List<AiMessage> history) {
        try {
            JSONObject root = new JSONObject();
            root.put("systemInstruction", new JSONObject()
                    .put("parts", new JSONArray().put(new JSONObject().put("text", SYSTEM_PROMPT))));

            JSONArray contents = new JSONArray();
            for (AiMessage msg : history) {
                if (msg.getRole() == AiMessage.Role.TYPING)
                    continue;
                if (msg.getRole() == AiMessage.Role.BOT && msg.isStreaming())
                    continue;

                contents.put(new JSONObject()
                        .put("role", msg.getApiRole())
                        .put("parts", new JSONArray().put(new JSONObject().put("text", msg.getContent()))));
            }
            root.put("contents", contents);

            return root.toString();

        } catch (JSONException e) {
            Log.e(TAG, "Failed to build request body", e);
            return "{}";
        }
    }

    private String parseApiError(int code) {
        switch (code) {
            case 400:
                return "Yêu cầu không hợp lệ hoặc API Key sai. Vui lòng kiểm tra key Gemini.";
            case 403:
                return "API Key không có quyền truy cập Gemini API.";
            case 404:
                return "Model không tồn tại. Vui lòng kiểm tra tên model.";
            case 429:
                return "Đã vượt giới hạn yêu cầu. Vui lòng thử lại sau vài giây.";
            case 500:
            case 503:
                return "Server AI đang bận. Vui lòng thử lại.";
            default:
                return "Lỗi kết nối (code " + code + "). Vui lòng thử lại.";
        }
    }
}
