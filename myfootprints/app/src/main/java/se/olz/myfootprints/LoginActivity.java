package se.olz.myfootprints;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private WebHandler web;
    private DBUsers users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        web = new WebHandler(this);
        users = new DBUsers(this);
        if (users.isLoggedIn()) {
            if (web.pull() == 0) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                users.logout();
            }
        }
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        EditText eEmail = (EditText)findViewById(R.id.login_email);
        EditText ePassword = (EditText)findViewById(R.id.login_password);
        TextView eError = (TextView)findViewById(R.id.login_error);
        String email = eEmail.getText().toString();
        String lowerEmail = email.toLowerCase();
        String password = ePassword.getText().toString();

        if (!EmailValidator.getInstance().isValid(lowerEmail)){
            eError.setText(R.string.invalid_email);
        } else if (password.isEmpty()) {
            eError.setText(R.string.empty_field);
        } else {
            int loginSuccess = web.login(lowerEmail, password);
            if (loginSuccess == 0) {
                users.login(lowerEmail);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (loginSuccess == -1){
                eError.setText(R.string.wrong_password);
            } else {
                eError.setText(R.string.connection_error);
             }
        }
    }
}
