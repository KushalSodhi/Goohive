package com.example.goohive;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    List<String> files;
    OnDeleteClick listener;

    public interface OnDeleteClick {
        void onDelete(String filename);
    }

    public FileAdapter(List<String> files, OnDeleteClick listener) {
        this.files = files;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        Button deleteBtn;

        public ViewHolder(View v) {
            super(v);
            fileName = v.findViewById(R.id.fileName);
            deleteBtn = v.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String file = files.get(position);
        holder.fileName.setText(file);

        // DELETE
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(file));

        // OPEN FILE
        holder.itemView.setOnClickListener(v -> {
            try {
                File fileObj = new File(v.getContext().getFilesDir(), file);

                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(
                        androidx.core.content.FileProvider.getUriForFile(
                                v.getContext(),
                                v.getContext().getPackageName() + ".provider",
                                fileObj
                        ),
                        "*/*"
                );

                intent.setFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                v.getContext().startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setAlpha(0f);
            holder.itemView.animate().alpha(1f).setDuration(300).start();
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
