package net.penguincoders.doit;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ParentTask extends AppCompatActivity {

    private TextView text1 ;
    private TextView text2 ;
    private TextView text3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_task);
        Intent intent = getIntent();
        intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        text1 = findViewById(R.id.textView1);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);

        text1.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE1));
        text2.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE2));
        text3.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE3));


        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text1.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text2.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text3.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}