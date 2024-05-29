package love.smartalk.game2048;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.FileNotFoundException;

import love.smartalk.game2048.utils.SPData;
import love.smartalk.game2048.utils.ShareUtils;
import love.smartalk.game2048.widget.Love2048Layout;

public class MainActivity extends BaseActivity implements Love2048Layout.OnLove2048Listener {

    TextView tv_score;
    TextView tv_best_score;
    Love2048Layout ll_game_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_score =findViewById(R.id.tv_score);
        tv_best_score =findViewById(R.id.tv_best_score);
        ll_game_view =findViewById(R.id.ll_game_view);
        findViewById(R.id.tv_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_game_view.restart();

            }
        });

        initView();
    }

    private void initView(){
        ll_game_view.setLove2048Listener(this);
        tv_best_score.setText("BestScore: " + SPData.getBestScore());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_share){
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScoreChanged(int score) {
        if (SPData.getBestScore() < score){
            tv_best_score.setText("BestScore: "+ score);
            SPData.saveBestScore(score);
        }

        String scoreStr = "CurrentScore: " + score;
        tv_score.setText(scoreStr);
    }

    @Override
    public void onGameOver() {
        showGameOver();
    }

    private void showGameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Love2048");
        builder.setMessage("Small CaiBi, Game over! Try again? ");
        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ll_game_view.restart();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
