package com.example.otlcse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context contect;
    public ArrayList<HelperClass> list;
    public onItemClickListener listener;


    public MyAdapter(Context contect, ArrayList<HelperClass> list) {
        this.contect = contect;
        this.list = list;
    }


    public interface onItemClickListener {
        void onItemCLick(int position);
    }

    public void setonItemClickListener(onItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contect).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v, listener, list);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HelperClass helperClass = list.get(position);
        holder.name.setText(helperClass.getFirstname());
        holder.email.setText(helperClass.getEmail());


        holder.btndetail.setOnClickListener((view) -> {
            HelperClass helperClass1 = list.get(position);
            System.out.println(helperClass1.getFirstname());
            Intent newActivity1 = new Intent(contect, detailuser.class);
            newActivity1.putExtra("userid", helperClass.getKey());
            contect.startActivity(newActivity1);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button btndelete, btndetail;
        TextView name, email;
        private MyAdapter myAdapter;

        public MyViewHolder(@NonNull View itemView, onItemClickListener listener, ArrayList<HelperClass> list) {
            super(itemView);
            name = itemView.findViewById(R.id.txtname);
            email = itemView.findViewById(R.id.txtemail);
            btndelete = itemView.findViewById(R.id.btndelete);
            btndetail = itemView.findViewById(R.id.btnview);

            btndelete.setOnClickListener((view) -> {
                HelperClass helperClass1 = list.get(getAdapterPosition());
                System.out.println(helperClass1.getFirstname());
                FirebaseDatabase.getInstance().getReference().child("users").child(helperClass1.getKey()).removeValue();
                listener.onItemCLick(getAdapterPosition());

            });


        }
    }
}
