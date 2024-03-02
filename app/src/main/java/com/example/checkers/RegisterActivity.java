package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    Button bt_save;
    EditText et_user, et_pass, et_repass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt_save = findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        et_user = findViewById(R.id.et_user);
        et_pass = findViewById(R.id.et_pass);
        et_repass = findViewById(R.id.et_repass);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v == bt_save){
            if (et_user.getText() == null || et_pass.getText() == null || et_repass.getText() == null || et_user.getText().toString().equals("") || et_pass.getText().toString().equals("") || et_repass.getText().toString().equals("")){
                Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!et_pass.getText().toString().equals(et_repass.getText().toString())){
                Toast.makeText(this, "Password isn't matching", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = et_user.getText()+"";
            String password = et_pass.getText()+"";
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user == null)
            return;
        Intent intent = new Intent();
        intent.putExtra("email", et_user.getText().toString());
        intent.putExtra("pass", et_pass.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}