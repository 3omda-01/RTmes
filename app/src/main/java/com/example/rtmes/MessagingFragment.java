package com.example.rtmes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.rtmes.databinding.FragmentMessagingBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessagingFragment extends Fragment {

    private FragmentMessagingBinding binding;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMessagingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageInput.getText() != null 
                    ? binding.messageInput.getText().toString() 
                    : "";
            if (!message.isEmpty()) {
                sendMessage(message);
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        binding.receiveButton.setOnClickListener(v -> receiveMessage());
    }

    private void sendMessage(String message) {
        String id = databaseReference.push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (id != null) {
            Message msg = new Message(message, timestamp);
            databaseReference.child(id).setValue(msg)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                        binding.messageInput.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to send: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void receiveMessage() {
        binding.messageCard.setVisibility(View.VISIBLE);
        binding.receivedMessage.setText("Message received!");
        Toast.makeText(requireContext(), "Checking for messages...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class Message {
        public String text;
        public String timestamp;

        public Message() {}

        public Message(String text, String timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
