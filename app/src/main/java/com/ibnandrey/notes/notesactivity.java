package com.ibnandrey.notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class notesactivity extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;


    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;


    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesactivity);

        mcreatenotesfab=findViewById(R.id.createnotefab);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("Notes");

        mcreatenotesfab.setOnClickListener(v -> startActivity(new Intent(notesactivity.this,createnote.class)));


        Query query=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes= new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        noteAdapter= new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {


                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colourcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(v -> {

                    Intent intent=new Intent(v.getContext(),notedetails.class);
                    intent.putExtra("title",firebasemodel.getTitle());
                    intent.putExtra("content",firebasemodel.getContent());
                    intent.putExtra("noteId",docId);

                    v.getContext().startActivity(intent);

                });

                popupbutton.setOnClickListener(v -> {

                    PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Редагувати").setOnMenuItemClickListener(item -> {

                        Intent intent=new Intent(v.getContext(),editnoteactivity.class);
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("content",firebasemodel.getContent());
                        intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);
                        return false;
                    });

                    popupMenu.getMenu().add("Видалити").setOnMenuItemClickListener(item -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(notesactivity.this);
                        builder.setTitle("Видалити");
                        builder.setMessage("Ви точно хочете видалити замітку?");
                        builder.setPositiveButton("Так", (dialogInterface, i1) -> {
                            DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                            documentReference.delete().addOnSuccessListener(aVoid -> Toast.makeText(v.getContext(),"Успішно видалено",Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(v.getContext(),"Помилка видалення",Toast.LENGTH_SHORT).show());
                        });
                        builder.setNegativeButton("Ні", (dialogInterface, i1) -> {});
                        builder.create().show();
                        return false;
                    });
                    popupMenu.show();
                });
            }
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
               return new NoteViewHolder(view);
            }
        };

        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);

            Animation translate_anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_anim);
            mnote.setAnimation(translate_anim);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(notesactivity.this,MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       if(noteAdapter!=null)
       {
           noteAdapter.stopListening();
       }
    }


    private int getRandomColor()
    {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}