package com.myprey.ultimatetic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Ramanan on 7/22/2015.
 */
public class Engine extends View {
    Context cont;
    MediaPlayer player, xm, om;
    Canvas ref;
    float p, ax, ay;
    int animex, animey;
    int i, j;//iterators
    int viewcounter = 0;
    float width, height;
    Random random=new Random();
    float slope;
    int in, move = 1;
    Paint brush;
    int state = 1, user = 4;
    Rect backbound;
    Bitmap wood;
    int[][] brd = new int[3][3];
    int[][] mbrd = new int[9][9];
    long temp;

    int k,l,emptyspaces,emptycount;
    int ci,cj;
    int result, gmcmpt = 0, sum, sum1, sum2, sum3, o;

    Runnable r = new Runnable() {
        @Override
        public void run() {
            long mov = move;
            long curtime = System.currentTimeMillis();
            while (System.currentTimeMillis() < curtime + 10000 && move != 1 && mov == move) {
                temp = curtime + 10000 - System.currentTimeMillis();
            }
            cj = animex % 3;
            ci = animey % 3;
            if (mov == move&&brd[ci][cj]==0) {
                emptyspaces=0;
                ci *= 3;
                cj *= 3;
                for(k=ci;k<3+ci;k++)for(l=cj;l<cj+3;l++)if(mbrd[k][l]==0)emptyspaces++;
                emptycount=random.nextInt(emptyspaces)+1;
                boolean f=true;
                emptyspaces=0;
                for(k=ci;k<3+ci&&f;k++){
                    for(l=cj;l<cj+3&&f;l++){
                        if(mbrd[k][l]==0){
                            emptyspaces++;
                            if(emptyspaces==emptycount){
                                if(move%2==1)user=4;
                                else user=1;
                                if(user==4)xm.start();
                                else om.start();
                                mbrd[k][l]=user;
                                move++;
                                animey=k;
                                animex=l;
                                f=false;
                                minibrd();
                            }
                        }
                    }
                }
                t=new Thread(r);
                t.start();
            }
        }
    };
    Thread t;

    public Engine(Context context) {
        super(context);
        cont = context;
    }

    public Engine(Context context, AttributeSet attrs) {
        super(context, attrs);
        cont = context;
    }

    public Engine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cont = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ref = canvas;
        if (viewcounter == 0) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            brush = new Paint();
            backbound = new Rect();
            wood = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.wood),
                    (int) width, (int) height, true);
            slope = height * 3 / width / 4;
            player = MediaPlayer.create(cont, R.raw.knock);
            xm = MediaPlayer.create(cont, R.raw.playx);
            om = MediaPlayer.create(cont, R.raw.zoop);
        }
        if (state == 1) {
            staticdisplayboard();
            if (move != 1 && brd[animey % 3][animex % 3] == 0) overwrite();
            sum2 = 0;
            sum3 = 0;
            for (i = 0; i < 3; i++) {
                sum = 0;
                sum1 = 0;
                for (j = 0; j < 3; j++) {
                    if (i == j) sum2 += brd[i][j];
                    if (i + j == 2) sum3 += brd[i][j];
                    sum += brd[i][j];
                    sum1 += brd[j][i];
                }
                if (sum == 3 * user) {
                    gmcmpt = 1;
                    result = user;
                } else if (sum1 == 3 * user) {
                    gmcmpt = 1;
                    result = user;
                }
            }
            if (sum2 == 3 * user) {
                gmcmpt = 1;
                result = user;
            } else if (sum3 == 3 * user) {
                gmcmpt = 1;
                result = user;
            }

        }
        for (i = 0, o = 0; i < 3; i++) for (j = 0; j < 3; j++) if (brd[i][j] == 0) o++;

        if (state == 2) {
            staticdisplayboard();
            brush.setStrokeWidth(15);
            brush.setColor(Color.WHITE);
            brush.setStyle(Paint.Style.STROKE);
            if (p < ax + width / 9) {
                if (in == 4) {
                    ref.drawLine(ax, ay, p, slope * (p - ax) + ay, brush);
                    ref.drawLine(ax, ay + height / 12, p, -slope * (p - ax) + ay + height / 12, brush);
                    p += 3;
                }
                if (in == 1) {
                    ref.drawCircle(ax + width / 18, ay + height / 24, (p - ax) / 2, brush);
                    p += 3;
                }
            } else {
                state = 1;
                if (mbrd[animey][animex] == 0) {
                    mbrd[animey][animex] = in;
                    minibrd();
                    move++;
                    t = new Thread(r);
                    t.start();
                }
            }
        }

        if (o == 0) gmcmpt = 1;
        if (gmcmpt == 1 && viewcounter == 1) {
            if (result == 4)
                Toast.makeText(cont, "X Wins\nTOUCH the board to PLAY again", Toast.LENGTH_LONG).show();
            if (result == 1)
                Toast.makeText(cont, "O WINS\nTOUCH the board to PLAY again", Toast.LENGTH_LONG).show();
            if (result == 0)
                Toast.makeText(cont, "DRAW match\nTOUCH the board to PLAY again", Toast.LENGTH_LONG).show();
            viewcounter++;
        }
        if (viewcounter == 0) viewcounter++;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        int iy = (int) (x * 3 / width), ix = (int) (y * 4 / height);
        int cy = (int) (x * 9 / width), cx = (int) (y * 12 / height);
        int mevent = event.getAction();
        switch (mevent) {
            case MotionEvent.ACTION_DOWN:
                if (state == 1) {
                    if (gmcmpt == 1) {
                        player.start();
                        if (x > width / 2 - backbound.width() / 2 && x < width / 2 + backbound.width() / 2 && y < height - backbound.height() / 2 && y > height - 3 * backbound.height() / 2) {
                            Intent i = new Intent(cont, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("BACK", true);
                            cont.startActivity(i);
                            return true;
                        }
                        Intent i = new Intent(cont, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("BACK", true);
                        cont.startActivity(i);
                    }
                    if (x > width / 2 - backbound.width() / 2 && x < width / 2 + backbound.width() / 2 && y < height - backbound.height() / 2 && y > height - 3 * backbound.height() / 2) {
                        /*player.start();
                        Intent i = new Intent(cont, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("BACK", true);
                        cont.startActivity(i);*/
                        mbrd = new int[9][9];
                        brd = new int[9][9];
                        animex = 0;
                        animey = 0;
                        move = 1;
                    }

                    //TOUCHES
                    if (x > 0 && x < width && y > 0 && y < 3 * height / 4 && gmcmpt == 0 && move == 1) {
                        if (move % 2 == 1) user = 4;
                        else user = 1;
                        animex = (int) (x * 9 / width);
                        animey = (int) (y * 12 / height);
                        p = animex * width / 9;
                        ax = p;
                        ay = animey * height / 12;
                        state = 2;
                        in = user;
                        if (user == 4) xm.start();
                        if (user == 1) om.start();
                        return true;
                    } else if (x > 0 && x < width && y > 0 && y < 3 * height / 4 && gmcmpt == 0 && brd[animey % 3][animex % 3] == 0 && ix == animey % 3 && iy == animex % 3 && move != 1 && mbrd[cx][cy] == 0) {
                        if (move % 2 == 1) user = 4;
                        else user = 1;
                        animex = (int) (x * 9 / width);
                        animey = (int) (y * 12 / height);
                        p = animex * width / 9;
                        ax = p;
                        ay = animey * height / 12;
                        state = 2;
                        in = user;
                        if (user == 4) xm.start();
                        if (user == 1) om.start();
                        return true;
                    } else if (x > 0 && x < width && y > 0 && y < 3 * height / 4 && gmcmpt == 0 && brd[animey % 3][animex % 3] != 0 && (ix != animey % 3 || iy != animex % 3) && move != 1 && mbrd[cx][cy] == 0) {
                        if (brd[ix][iy] == 0) {
                            if (move % 2 == 1) user = 4;
                            else user = 1;
                            animex = (int) (x * 9 / width);
                            animey = (int) (y * 12 / height);
                            p = animex * width / 9;
                            ax = p;
                            ay = animey * height / 12;
                            state = 2;
                            in = user;
                            if (user == 4) xm.start();
                            if (user == 1) om.start();
                            return true;
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void staticdisplayboard() {
        brush.setStrokeWidth(10);
        brush.setColor(Color.rgb(150, 111, 51));
        brush.setStyle(Paint.Style.FILL);
        ref.drawRect(0, 0, width, height, brush);
        ref.drawBitmap(wood, 0, 0, null);
        brush.setColor(Color.BLACK);
        brush.setTextSize(height / 12);
        brush.setStyle(Paint.Style.STROKE);
        brush.getTextBounds("CLEAR", 0, 5, backbound);
        ref.drawText("CLEAR", width / 2 - backbound.width() / 2, height - backbound.height() / 2, brush);
        ref.drawLine(0, 3 * height / 4 + height / 8,temp*(width/10000) , 3 * height / 4 + height / 8,brush);
        brush.setColor(Color.WHITE);
        brush.setStrokeWidth(15);
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                if (mbrd[i][j] == 1) {
                    ref.drawCircle(j * width / 9 + width / 18, i * height / 12 + height / 24, width / 18 - 10, brush);
                } else if (mbrd[i][j] == 4) {
                    ref.drawLine(j * width / 9 + 10, i * height / 12 + 10, j * width / 9 + width / 9 - 10, i * height / 12 + height / 12 - 10, brush);
                    ref.drawLine(j * width / 9 + 10, i * height / 12 + height / 12 - 10, j * width / 9 + width / 9 - 10, i * height / 12 + 10, brush);
                }
            }
        }
        brush.setColor(Color.BLACK);
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (brd[i][j] == 1) {
                    ref.drawCircle(j * width / 3 + width / 6, i * height / 4 + height / 8, width / 6 - 10, brush);
                } else if (brd[i][j] == 4) {
                    ref.drawLine(j * width / 3 + 10, i * height / 4 + 10, j * width / 3 + width / 3 - 10, i * height / 4 + height / 4 - 10, brush);
                    ref.drawLine(j * width / 3 + 10, i * height / 4 + height / 4 - 10, j * width / 3 + width / 3 - 10, i * height / 4 + 10, brush);
                }
            }
        }
        brush.setStrokeWidth(4);
        brush.setColor(Color.WHITE);

        ref.drawLine(width / 9, 0, width / 9, 3 * height / 4, brush);
        ref.drawLine(2 * width / 9, 0, 2 * width / 9, 3 * height / 4, brush);
        ref.drawLine(4 * width / 9, 0, 4 * width / 9, 3 * height / 4, brush);
        ref.drawLine(5 * width / 9, 0, 5 * width / 9, 3 * height / 4, brush);
        ref.drawLine(7 * width / 9, 0, 7 * width / 9, 3 * height / 4, brush);
        ref.drawLine(8 * width / 9, 0, 8 * width / 9, 3 * height / 4, brush);

        ref.drawLine(0, height / 12, width, height / 12, brush);
        ref.drawLine(0, height / 6, width, height / 6, brush);
        ref.drawLine(0, height / 3, width, height / 3, brush);
        ref.drawLine(0, 5 * height / 12, width, 5 * height / 12, brush);
        ref.drawLine(0, 7 * height / 12, width, 7 * height / 12, brush);
        ref.drawLine(0, 8 * height / 12, width, 8 * height / 12, brush);

        brush.setColor(Color.CYAN);
        brush.setStrokeWidth(8);
        ref.drawLine(width / 3, 0, width / 3, 3 * height / 4, brush);
        ref.drawLine(width / 3 * 2, 0, width / 3 * 2, 3 * height / 4, brush);
        ref.drawLine(0, height / 4, width, height / 4, brush);
        ref.drawLine(0, height / 2, width, height / 2, brush);

    }

    public void minibrd() {
        int ybrd = animex / 3;
        int xbrd = animey / 3;
        xbrd *= 3;
        ybrd *= 3;
        sum2 = 0;
        sum3 = 0;
        for (i = xbrd; i < xbrd + 3; i++) {
            sum = 0;
            sum1 = 0;
            for (j = ybrd; j < ybrd + 3; j++) {
                if (i - xbrd == j - ybrd) sum2 += mbrd[i][j];
                if (i + j - xbrd - ybrd == 2) sum3 += mbrd[i][j];
                sum += mbrd[i][j];
            }
            if (sum == 3 * user) {
                brd[animey / 3][animex / 3] = user;
                return;
            }
        }
        if (sum2 == 3 * user) {
            brd[animey / 3][animex / 3] = user;
            return;
        } else if (sum3 == 3 * user) {
            brd[animey / 3][animex / 3] = user;
            return;
        }
        //column check
        for (j = ybrd; j < ybrd + 3; j++) {
            sum1 = 0;
            for (i = xbrd; i < xbrd + 3; i++) sum1 += mbrd[i][j];
            if (sum1 == 3 * user) {
                brd[animey / 3][animex / 3] = user;
                return;
            }
        }
        for (i = xbrd, o = 0; i < xbrd + 3; i++)
            for (j = ybrd; j < ybrd + 3; j++) if (mbrd[i][j] == 0) o++;
        if (o == 0 && brd[animey / 3][animex / 3] == 0) brd[animey / 3][animex / 3] = -8;
    }

    public void overwrite() {
        brush.setColor(Color.RED);
        if (animey % 3 == 0 && animex % 3 == 0) {
            ref.drawLine(width / 3, 0, width / 3, height / 4, brush);
            ref.drawLine(0, height / 4, width / 3, height / 4, brush);
        }
        if (animey % 3 == 0 && animex % 3 == 2) {
            ref.drawLine(2 * width / 3, 0, 2 * width / 3, height / 4, brush);
            ref.drawLine(2 * width / 3, height / 4, width, height / 4, brush);
        }
        if (animey % 3 == 2 && animex % 3 == 0) {
            ref.drawLine(0, height / 2, width / 3, height / 2, brush);
            ref.drawLine(width / 3, height / 2, width / 3, 3 * height / 4, brush);
        }
        if (animey % 3 == 2 && animex % 3 == 2) {
            ref.drawLine(2 * width / 3, height / 2, width, height / 2, brush);
            ref.drawLine(2 * width / 3, height / 2, 2 * width / 3, 3 * height / 4, brush);
        }
        if (animey % 3 == 1 && animex % 3 == 1) {
            ref.drawLine(width / 3, height / 4, 2 * width / 3, height / 4, brush);
            ref.drawLine(width / 3, height / 2, 2 * width / 3, height / 2, brush);
            ref.drawLine(width / 3, height / 4, width / 3, height / 2, brush);
            ref.drawLine(2 * width / 3, height / 4, 2 * width / 3, height / 2, brush);
        }
        if (animey % 3 == 0 && animex % 3 == 1) {
            ref.drawLine(width / 3, 0, width / 3, height / 4, brush);
            ref.drawLine(width / 3, height / 4, 2 * width / 3, height / 4, brush);
            ref.drawLine(2 * width / 3, 0, 2 * width / 3, height / 4, brush);
        }
        if (animey % 3 == 1 && animex % 3 == 0) {
            ref.drawLine(0, height / 4, width / 3, height / 4, brush);
            ref.drawLine(width / 3, height / 4, width / 3, height / 2, brush);
            ref.drawLine(0, height / 2, width / 3, height / 2, brush);
        }
        if (animey % 3 == 1 && animex % 3 == 2) {
            ref.drawLine(2 * width / 3, height / 4, width, height / 4, brush);
            ref.drawLine(2 * width / 3, height / 4, 2 * width / 3, height / 2, brush);
            ref.drawLine(2 * width / 3, height / 2, width, height / 2, brush);
        }
        if (animey % 3 == 2 && animex % 3 == 1) {
            ref.drawLine(width / 3, height / 2, width / 3, 3 * height / 4, brush);
            ref.drawLine(2 * width / 3, height / 2, width / 3, height / 2, brush);
            ref.drawLine(2 * width / 3, height / 2, 2 * width / 3, 3 * height / 4, brush);
        }
    }

}
