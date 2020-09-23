package com.example.resfeber.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resfeber.R;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
    private int noOfTickets;

    public TicketAdapter(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_card, parent, false);
        return new TicketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.bind(position);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return noOfTickets;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.qrCode)
        ImageView qrCodeImage;
        @BindView(R.id.current_total_textView)
        TextView totalText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) throws WriterException {
            String textPos = (position + 1) + "/" + noOfTickets;
            totalText.setText(textPos);

            QRGEncoder qrgEncoder = new QRGEncoder(position + "", null, QRGContents.Type.TEXT, 400);
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qrCodeImage.setImageBitmap(bitmap);
        }
    }
}
