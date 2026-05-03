package com.example.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private EditText etRegLogin;
    private EditText etRegEmail;
    private EditText etRegPassword;
    private EditText etRegRepeatPassword;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = AppDatabase.getInstance(this);

        etRegLogin = findViewById(R.id.etRegLogin);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegRepeatPassword = findViewById(R.id.etRegRepeatPassword);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String login = etRegLogin.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString();
        String repeatPassword = etRegRepeatPassword.getText().toString();

        if (!Validation.isLoginValid(login)) {
            Toast.makeText(this, "Логин должен быть не короче 3 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validation.isEmailValid(email)) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validation.isPasswordValid(password)) {
            Toast.makeText(this, "Пароль должен быть не короче 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppUser userByLogin = database.userDao().getByLogin(login);
            AppUser userByEmail = database.userDao().getByEmail(email);

            if (userByLogin != null || userByEmail != null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            AppUser user = new AppUser();
            user.login = login;
            user.email = email;
            user.password = password;

            database.userDao().insert(user);

            runOnUiThread(() -> {
                Toast.makeText(this, "Регистрация выполнена", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}