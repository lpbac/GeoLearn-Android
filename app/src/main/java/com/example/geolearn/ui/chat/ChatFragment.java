package com.example.geolearn.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolearn.R;
import com.example.geolearn.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView rcv;
    private ConversationAdapter adapter;
    private EditText edtSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rcv = view.findViewById(R.id.rcvConversation);
        edtSearch = view.findViewById(R.id.edtSearch);

        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter
        adapter = new ConversationAdapter();
        rcv.setAdapter(adapter);

        // Xử lý tìm kiếm
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s.toString());
                }
                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại để cập nhật thứ tự và trạng thái Đã xem
        if (adapter != null) {
            adapter.reloadData();
        }
    }

    // --- Adapter ---
    class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

        // Mỗi mục: {tên hiển thị, mã phòng}. Mã phòng tính theo ID nên không nhầm khi trùng tên
        private List<String[]> originalList;
        private List<String[]> displayList;
        private int myId;

        public ConversationAdapter() {
            originalList = new ArrayList<>();
            displayList = new ArrayList<>();

            // Lấy ID của mình
            SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            myId = prefs.getInt("KEY_USER_ID", -1);

            reloadData(); // Load dữ liệu lần đầu
        }

        // Hàm load và SẮP XẾP dữ liệu
        public void reloadData() {
            originalList.clear();

            // 1. Lấy danh sách cơ bản
            String myClass = dbHelper.getUserClass(myId);
            if (!myClass.isEmpty()) {
                originalList.add(new String[] { "Lớp " + myClass, "Lớp " + myClass });
                for (android.util.Pair<Integer, String> t : dbHelper.getTeachersByClass(myClass)) {
                    originalList.add(new String[] { t.second + " (GV)", privateRoomId(t.first) });
                }
                for (android.util.Pair<Integer, String> st : dbHelper.getClassmates(myClass, myId)) {
                    originalList.add(new String[] { st.second, privateRoomId(st.first) });
                }
            } else {
                originalList.add(new String[] { "Hỗ trợ viên", "Hỗ trợ viên" });
            }

            // 2. SẮP XẾP: Ai nhắn tin mới nhất thì đẩy lên đầu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                originalList.sort((e1, e2) -> {
                    long time1 = dbHelper.getLastMessageTime(e1[1]);
                    long time2 = dbHelper.getLastMessageTime(e2[1]);

                    // Sắp xếp giảm dần (time lớn hơn đứng trước)
                    return Long.compare(time2, time1);
                });
            }

            // 3. Hiển thị
            displayList.clear();
            displayList.addAll(originalList);
            notifyDataSetChanged();
        }

        public void filter(String query) {
            displayList.clear();
            if (query.isEmpty()) {
                displayList.addAll(originalList);
            } else {
                for (String[] entry : originalList) {
                    if (entry[0].toLowerCase().contains(query.toLowerCase())) {
                        displayList.add(entry);
                    }
                }
            }
            notifyDataSetChanged();
        }

        // Phòng chat riêng: "idNhỏ_idLớn"
        private String privateRoomId(int partnerId) {
            return (myId < partnerId) ? (myId + "_" + partnerId) : (partnerId + "_" + myId);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String chatName = displayList.get(position)[0];
            String roomId = displayList.get(position)[1];
            holder.tvName.setText(chatName);

            // 2. Lấy tin nhắn cuối cùng
            String lastMsg = dbHelper.getLastMessage(roomId, myId);

            // 3. Kiểm tra TIN CHƯA ĐỌC
            boolean hasUnread = dbHelper.hasUnreadMessage(roomId, myId);

            if (lastMsg.isEmpty()) {
                holder.tvLastMessage.setText("Chưa có tin nhắn");
                holder.tvLastMessage.setTypeface(null, Typeface.ITALIC);
                holder.tvLastMessage.setTextColor(holder.itemView.getContext().getColor(R.color.colorTextSecondary));
                holder.tvName.setTypeface(null, Typeface.NORMAL);
            } else {
                holder.tvLastMessage.setText(lastMsg);

                if (hasUnread) {
                    // --- NẾU CÓ TIN MỚI: IN ĐẬM ---
                    holder.tvLastMessage.setTypeface(null, Typeface.BOLD);
                    holder.tvLastMessage.setTextColor(holder.itemView.getContext().getColor(R.color.colorTextPrimary));
                    holder.tvName.setTypeface(null, Typeface.BOLD); // Tên người cũng đậm
                } else {
                    // --- NẾU ĐÃ ĐỌC: BÌNH THƯỜNG ---
                    holder.tvLastMessage.setTypeface(null, Typeface.NORMAL);
                    holder.tvLastMessage.setTextColor(holder.itemView.getContext().getColor(R.color.colorTextSecondary));
                    holder.tvName.setTypeface(null, Typeface.NORMAL);
                }
            }
            holder.viewUnreadDot.setVisibility(hasUnread ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("CHAT_NAME", chatName); // Tên hiển thị (vẫn giữ chữ GV cho đẹp)

                // --- QUAN TRỌNG: Gửi kèm ID phòng chuẩn sang ---
                intent.putExtra("ROOM_ID", roomId);

                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return displayList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvLastMessage;
            View viewUnreadDot;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName); // Đảm bảo ID đúng trong item_conversation.xml
                tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
                viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
            }
        }
    }
}