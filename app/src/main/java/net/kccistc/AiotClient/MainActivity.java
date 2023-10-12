package net.kccistc.AiotClient;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

  static final int REQUEST_CODE_LOGIN_ACTIVITY = 100;
  int bottomNavigationIndex = 0;
  static MainHandler mainHandler;
  ClientThread clientThread;
  Fragment1Home fragment1Home;
  Fragment2Dashboard fragment2Dashboard;
  Fragment3Telnet fragment3Telnet;
  Fragment4WebvView fragment4WebvView;
  Database database;
  public SQLiteDatabase SQLdb;

  @Override
  protected void onCreate(Bundle savedInstanceState) // Start of MainActivity
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    fragment1Home = new Fragment1Home();
    fragment2Dashboard = new Fragment2Dashboard();
    fragment3Telnet = new Fragment3Telnet();
    fragment4WebvView = new Fragment4WebvView();

    database = new Database(getBaseContext());
    SQLdb = database.getWritableDatabase();
    String dbName = database.getDatabaseName();
    if (dbName == null) {
      database.onCreate(SQLdb);
    }

    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1Home).commit();

    BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
    bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

          case R.id.tab1:
//            Toast.makeText(getApplicationContext(), "첫 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment1Home).commit();
            bottomNavigationIndex = 0;
            return true;

          case R.id.tab2:
//            Toast.makeText(getApplicationContext(), "두 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment2Dashboard).commit();
            bottomNavigationIndex = 1;
            return true;

          case R.id.tab3:
//            Toast.makeText(getApplicationContext(), "세 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment3Telnet).commit();
            bottomNavigationIndex = 2;
            return true;

          case R.id.tab4:
//            Toast.makeText(getApplicationContext(), "세 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment4WebvView).commit();
            bottomNavigationIndex = 3;
            return true;
        }
        return false;
      }
    });
    mainHandler = new MainHandler();
    Intent intent = getIntent();
    if (intent != null) {
//      processIntent(intent);
    }
  }

  class MainHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      super.handleMessage(msg);
      Bundle bundle = msg.getData();
      String data = bundle.getString("msg");
      Log.d("MainHandler", data);
      if ((data.contains("New connect")) || (data.contains("Already log"))) {
        if (bottomNavigationIndex == 2)
          fragment3Telnet.newConnectedButtonEnable();
        return;
      }

      if (bottomNavigationIndex == 0) { // Home
        fragment1Home.recvDataProcess(data);
      }
      if (bottomNavigationIndex == 1) // Dashboard
        fragment2Dashboard.recvDataProcess(data);

      if (bottomNavigationIndex == 2) // Telnet
        fragment3Telnet.recvDataProcess(data);

      if (bottomNavigationIndex == 3) // WebView
        fragment4WebvView.recvDataProcess(data);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.login:
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.putExtra("serverIp", ClientThread.serverIp);
        intent.putExtra("serverPort", ClientThread.serverPort);
        intent.putExtra("clientId", ClientThread.clientId);
        intent.putExtra("clientPw", ClientThread.clientPw);
        startActivityForResult(intent, REQUEST_CODE_LOGIN_ACTIVITY); // Start Activity result -> intent
        break;

      case R.id.db_delete:
        database.deleteTable(SQLdb);
        String DB_PATH = "/data/data/" + getPackageName();
        String DB_NAME = database.DATABASE_NAME;
        String DB_FULLPATH = DB_PATH + "/database/" + DB_NAME;
        File dbFile = new File(DB_FULLPATH);
        if (dbFile.delete()) {
          Toast.makeText(MainActivity.this, DB_NAME + " 삭제 완료", Toast.LENGTH_SHORT).show();
        }

        break;

      case R.id.alarm:
        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(i);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_LOGIN_ACTIVITY) {
      if (resultCode == RESULT_OK) {
        ClientThread.serverIp = data.getStringExtra("serverIp");
        ClientThread.serverPort = data.getIntExtra("serverPort", ClientThread.serverPort);
        ClientThread.clientId = data.getStringExtra("clientId");
        ClientThread.clientPw = data.getStringExtra("clientPw");
        clientThread = new ClientThread(); // Thread Start
        clientThread.start();

      } else if (resultCode == RESULT_CANCELED) {
        if (clientThread != null) {
          clientThread.stopClient();
          ClientThread.socket = null;

        }
      }
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    Log.d("MainActivity", "onNewIntent 호출됨");
    if (intent != null) {
      processIntent(intent);
    }
    super.onNewIntent(intent);
  }

  private void processIntent(Intent intent) {
    String title = intent.getStringExtra("title");
    if (title == null) {
      Log.d("MainActivity", "title  is null.");
      return;
    }

    String body = intent.getStringExtra("body");
    Log.d("MainActivity", "Title : " + title + ", " + "Body : " + body);
    Toast.makeText(getApplicationContext(), "Title : " + title + ", " + "Body : " + body, Toast.LENGTH_LONG).show();
  }
}