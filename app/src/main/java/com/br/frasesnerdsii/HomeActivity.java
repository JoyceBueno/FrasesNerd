package com.br.frasesnerdsii;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.frasesnerdsii.helpers.ConfiguracaoFirebase;
import com.br.frasesnerdsii.helpers.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private ImageView imagemPerfil;

    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageReference;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imagemPerfil = findViewById(R.id.image_perfil);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        storageReference = ConfiguracaoFirebase.getReferenciaStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Frases Nerd");
        setSupportActionBar(toolbar);

        // Configurando a imagem
        imagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                // Verifica se a imagem foi escolhida e já faz o upload
                if( imagem != null) {
                    imagemPerfil.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Configurando o Storage
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("Perfil")
                            .child(idUsuarioLogado + "jpeg");

                    // Tarefa de Upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    // Em caso de falha no Upload
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this,
                                    "Erro ao fazer o upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(HomeActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    // Criando as opções do menu
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    // Verificando a opções selecionadas pelo usuário
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // Funão responsável por gerar a frase
    public void gerarNovaFrase(View view){

        //armazenando as frases que podem ser geradas
        String[] frases = {
                "Vida longa e próspera(Sr. Spock - StarTrek)",
                "Eu sou o Homem de Ferro!(Tony Stark)",
                "Que a Força esteja com você!(StarWars)",
                "Você não passará!(Gandalf)",
                "Esse é meu jeito ninja de ser!(Naruto)",
                "Tem uma cobra na minha bota(Woody)",
                "Tio Sam, Socorro!(Pica-Pau)",
                "Pouso de super-herói(Deadpool)",
                "Eu sou o Batman!(Batman)"
        };

        //VAR responsável por gerar os valores aleatórios
        // que serão utilizados para indicar a posição no vetor
        int numero = new Random().nextInt(9);

        //Mostrando a frase com base na posição soteada
        TextView frase = findViewById(R.id.textFraseGerada);
        frase.setText(frases[numero]);
    }
}