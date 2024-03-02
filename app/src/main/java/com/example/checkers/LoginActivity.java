package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    Button bt_save, bt_forgot;
    EditText et_user, et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_save = findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        bt_forgot = findViewById(R.id.bt_forgot);
        bt_forgot.setOnClickListener(this);
        et_user = findViewById(R.id.et_user);
        et_pass = findViewById(R.id.et_pass);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v == bt_save){
            if (et_user.getText() == null || et_pass.getText() == null || et_user.getText().toString().equals("") || et_pass.getText().toString().equals("")){
                Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = et_user.getText().toString();
            String password = et_pass.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Wrong email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            return;
        }
        if (v == bt_forgot){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_resetpass, null);
            builder.setView(dialogView);

            EditText editText = dialogView.findViewById(R.id.editText);
            Button button = dialogView.findViewById(R.id.button);

            AlertDialog dialog = builder.create();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = editText.getText().toString();
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Password reset email sent successfully
                                    // Show a success message or take appropriate action
                                    Toast.makeText(LoginActivity.this,"Get ready for your email delivery", Toast.LENGTH_LONG).show();
                                } else {
                                    // Password reset email sending failed
                                    // Show an error message or take appropriate action
                                    Toast.makeText(LoginActivity.this,"Wrong email", Toast.LENGTH_LONG).show();
                                }
                            });

                    dialog.dismiss(); // Close the dialog
                }
            });

            dialog.show();

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