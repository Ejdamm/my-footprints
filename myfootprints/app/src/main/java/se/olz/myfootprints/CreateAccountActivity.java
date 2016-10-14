package se.olz.myfootprints;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.validator.routines.EmailValidator;

public class CreateAccountActivity extends AppCompatActivity {
    private WebHandler web;
    public static final String TAG = CreateAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        web = new WebHandler(this);
    }

    public void createUser(View view) {
        EditText eEmail = (EditText)findViewById(R.id.create_email);
        EditText ePassword = (EditText)findViewById(R.id.create_password);
        EditText ePassword2 = (EditText)findViewById(R.id.create_password2);
        TextView eError = (TextView)findViewById(R.id.create_error);
        String email = eEmail.getText().toString();
        String lowerEmail = email.toLowerCase();
        String password = ePassword.getText().toString();
        String password2 = ePassword2.getText().toString();


        if (!password.equals(password2)) {
            eError.setText(R.string.match_passwords);
        } else if (!EmailValidator.getInstance().isValid(lowerEmail)){
            eError.setText(R.string.invalid_email);
        } else if (password.isEmpty()){
            eError.setText(R.string.empty_field);
        } else {
            int webError = web.createUser(lowerEmail, password);
            if(webError == -1) {
                eError.setText(R.string.email_exists);
            } else if (webError == -2) {
                eError.setText(R.string.connection_error);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }

        }
    }
}
