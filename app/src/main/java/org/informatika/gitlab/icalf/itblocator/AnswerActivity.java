package org.informatika.gitlab.icalf.itblocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

public class AnswerActivity extends AppCompatActivity {
  static final public long NIM = 13513004;
  static final public String host = "167.205.34.132";
//  static final public String host = "10.0.2.2";
  static final public short port = 3111;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_answer);
  }

  public void submitLocation(View view) {
    String[] locationList =
      {"gku_barat", "gku_timur", "intel", "cc_barat", "cc_timur", "dpr", "sunken", "perpustakaan", "pau", "kubus"};
    Spinner spinnerLocation = (Spinner)findViewById(R.id.spinner_location);
    String choice = locationList[spinnerLocation.getSelectedItemPosition()];

    // TODO : SEND REQUEST TO SERVER

    Intent returnIntent = new Intent();
    returnIntent.putExtra("result",
      "{\"status\":\"ok\",\"nim\":\"13512999\",\"longitude\":6.234123132,\"latitude\":0.1234123412,\"token\":\"21nu2f2n3rh23diefef23hr23ew\"}"
    );
    setResult(Activity.RESULT_OK, returnIntent);
    finish();
  }
}
