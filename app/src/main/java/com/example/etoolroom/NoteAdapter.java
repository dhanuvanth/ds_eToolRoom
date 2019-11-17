package com.example.etoolroom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Filterable {
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String htime;
    private String hdate;
    private String cdate;
    private String ctime;
    private String tool = null;
    private String plant = null;
    private String extr = null;
    private String size = null;
    private String radio = null;
    private String remarks1;
    private String remarks2;
    private String docId;
    private List<CardList> mCardlist;
    private List<CardList> mCardlistFull;

    NoteAdapter(List<CardList> cardList, Context context) {
        mCardlist = cardList;
        mContext = context;

        mCardlistFull = new ArrayList<>(cardList);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_list, viewGroup, false);
        return new NoteHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NoteHolder noteHolder, int i) {
        final CardList cardList;
        cardList = mCardlist.get(i);

        noteHolder.itemID.setText("ID :" + cardList.getID());
        noteHolder.itemSize.setText("Size :" + cardList.getSize());
        noteHolder.itemPlant.setText("Plant :" + cardList.getPlant());


        noteHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                db.collection("toolroomItems").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
                            Note note = queryDocument.toObject(Note.class);
                            note.setDocumentId(queryDocument.getId());

                            docId = note.getDocumentId();
                            if (docId.equals(cardList.getID())) {
                                tool = note.getTool_txt();
                                plant = note.getPlant_txt();
                                extr = note.getExtr_txt();
                                size = note.getSize();
                                radio = note.getRadio();
                                hdate = note.getHdate();
                                htime = note.getHtime();
                                cdate = note.getCdate();
                                ctime = note.getCtime();
                                remarks1 = note.getRemarks1();
                                remarks2 = note.getRemarks2();
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("ID : " + cardList.getID() + "\n\n" +
                                "Tool : " + tool + "\n\n" +
                                "Type : " + radio + "\n\n" +
                                "Size : " + size + "\n\n" +
                                "Plant : " + plant + "\n\n" +
                                "Extr : " + extr + "\n\n" +
                                "Handed over Date : " + hdate + "\n\n" +
                                "Handed over Time : " + htime + "\n\n" +
                                "Commitment Date : " + cdate + "\n\n" +
                                "Commitment Time : " + ctime + "\n\n" +
                                "Tool Room Remark : " + remarks1 + "\n\n" +
                                "Manufacturing Remark : " + remarks2
                        ).setCancelable(true).create().show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardlist.size();
    }

    @Override
    public Filter getFilter() {
        return filtedList;
    }

    private Filter filtedList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CardList> filtedItems = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtedItems.addAll(mCardlistFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CardList list : mCardlistFull) {
                    if (list.getSize().toLowerCase().contains(filterPattern) || list.getPlant().toLowerCase().contains(filterPattern)) {
                        filtedItems.add(list);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtedItems;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCardlist.clear();
            mCardlist.addAll((List) results.values);
            notifyDataSetChanged();
            Collections.reverse( mCardlist);
        }
    };

    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemID;
        private TextView itemSize;
        private TextView itemPlant;

        private ItemClickListener itemClickListener;

        NoteHolder(@NonNull View itemView) {
            super(itemView);
            itemID = itemView.findViewById(R.id.card_id);
            itemSize = itemView.findViewById(R.id.card_size);
            itemPlant = itemView.findViewById(R.id.card_plant);

            itemView.setOnClickListener(this);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.OnClick(v, getAdapterPosition());
        }
    }
}
