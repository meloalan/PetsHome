package com.example.projetosuporte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallations;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class Messagens extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagens);

        RecyclerView rv = findViewById(R.id.recycler_menssagens);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(Messagens.this, Chat.class);

                Messagens.UserItem userItem = (Messagens.UserItem) item;
                intent.putExtra("user", userItem.user);

                startActivity(intent);

            }
        });

        verifyAuthentication();



        fetchLastMessage();
        
    }



    private void fetchLastMessage() {
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("conversas")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                    if(documentChanges != null){
                        for (DocumentChange doc: documentChanges) {
                            if (doc.getType() == DocumentChange.Type.ADDED){
                               Conversas conversas = doc.getDocument().toObject(Conversas.class);

                               adapter.add(new ConversasItem(conversas));

                            }
                        }
                    }
                    }
                });

    }

    private void verifyAuthentication() {
        if (FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(Messagens.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.contatos:
                Intent intent = new Intent(Messagens.this, Contatos.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAuthentication();
                break;
            case R.id.alterar_foto:
                Intent intent1 = new Intent(Messagens.this, Cadastro.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    private class ConversasItem extends Item<ViewHolder>{

        private final Conversas conversas;

        private ConversasItem(Conversas conversas) {
            this.conversas = conversas;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {


           TextView username = viewHolder.itemView.findViewById(R.id.textView);
           TextView message = viewHolder.itemView.findViewById(R.id.textView2);
           ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);

           username.setText(conversas.getUsername());
           message.setText(conversas.getLastMessage());
            Picasso.get()
                    .load(conversas.getPhotoUrl())
                    .into(imgPhoto);

        }

        @Override
        public int getLayout() {
            return R.layout.item_user_conversas;
        }
    }

    private class UserItem extends Item<ViewHolder> {

        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtUsername = viewHolder.itemView.findViewById(R.id.textView);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);

            txtUsername.setText(user.getUsername());

            Picasso.get().load(user.getProfileUrl()).into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}