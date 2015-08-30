package com.example.image.mquotev2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.image.mquotev2.zoom.DynamicZoomControl;
import com.example.image.mquotev2.zoom.ImageZoomView;
import com.example.image.mquotev2.zoom.LongPressZoomListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.championswimmer.sfg.lib.SimpleFingerGestures;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener, AsyncResponse {
    SearchView SV;

    private DrawerLayout mDrawerLayout;

    int viewWidth = 0, viewHeight = 0;
    /**
     * Zoom control
     */
    private DynamicZoomControl mZoomControl;

    /**
     * Decoded bitmap image
     */
    private Bitmap mBitmap;

    /**
     * On touch listener for zoom view
     */
    private LongPressZoomListener mZoomListener;

    //View
    int screen_width, screen_height;
    ImageButton done;
    TextView guideText;
    EditText text_box;
    MagicTextView MTV;
    ImageZoomView view;
    ImageView textview;

    //Publish View
    ImageView phone, facebook, google;

    //Function Class
    ImageProcessing IP = new ImageProcessing();
    SingleScrollListView lv;
    InputMethodManager imm;
    SearchActivity SA;
    GestureDetector tapDetector;
    CustomFont cf;
    Image img = new Image();
    DownloadImage mDownload;

    //    Draw d = new Draw();
    CameraProcess CP;


    //Button
    ImageView browse, random, edit_image, change_font, et_background, camera_button, gallery_button, publish;

    //Layout
    RelativeLayout parentLayout, text_layout, blur_layout, select_image_layout, surface;
    private SimpleFingerGestures mySfg = new SimpleFingerGestures();

    //Variable for checking
    boolean isShow_browser, isShow_random = false, isKeyBoardVisible = true, isCancelDownload = false;
    boolean search = false, isChangedImage = false, begin = false, preview = false;
    boolean text_visible = false, isStroke = false, isScalingText = true;

    //Loop
    private int FRAME_RATE = 20;
    Handler h = new Handler();
    String kw;
    private ProgressDialog WaitDialog;

    //Edit Picture Menu
    SeekBar blur_SB, text_SB;
    CheckBox stroke;


    //Text
    int text_x = 0, text_y = 80, text_size = 0, text_size_in_textbox = 0;
    /**
     * Distance touch can wander before we think it's scrolling
     */
    private int mScaledTouchSlop;

    /**
     * Duration in ms before a press turns into a long press
     */
    private int mLongPressTimeout;

    /**
     * Vibrator for tactile feedback
     */
    private Vibrator mVibrator;


    public MainActivity() {
        isShow_browser = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.activity_main);
        //editText
        text_box = (EditText) findViewById(R.id.editText);
        text_box.setBackgroundColor(Color.TRANSPARENT);
        text_size_in_textbox = (int) text_box.getTextSize();

        //Keyboard control
        imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);

        parentLayout = (RelativeLayout) findViewById(R.id.main);

        tapDetector = new GestureDetector(this, new TapGestureListener());
        cf = new CustomFont(this);
        setup_views();
        setup_list_view();
        SA = new SearchActivity(this, lv);
        img.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);

        mZoomControl = new DynamicZoomControl();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);

        mZoomListener = new LongPressZoomListener(getApplicationContext());
        mZoomListener.setZoomControl(mZoomControl);

        view = (ImageZoomView) findViewById(R.id.view);
        view.setZoomState(mZoomControl.getZoomState());
        view.setImage(mBitmap);

        view.setOnTouchListener(mZoomListener);


        mZoomControl.setAspectQuotient(view.getAspectQuotient());

        resetZoomState();


        setup_Blur_SB();
        text_box.requestFocus(0);
        text_box.setSelection(0);
        text_box.setHint("");
        imm.showSoftInput(text_box, InputMethodManager.SHOW_IMPLICIT);

        mySfg.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, long gestureDuration) {
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, long gestureDuration) {
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, long gestureDuration) {
                if (gestureDuration >= 100) get_next_image();
                return false;
            }

            @Override
            public boolean onSwipeRight(int fingers, long gestureDuration) {
                if (gestureDuration >= 100) get_previous_image();
                return false;
            }

            @Override
            public boolean onPinch(int fingers, long gestureDuration) {
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, long gestureDuration) {
                return false;
            }
        });
        parentLayout.setOnTouchListener(mySfg);
        surface = (RelativeLayout) findViewById(R.id.layout_surface);
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        mScaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mVibrator = (Vibrator) this.getSystemService("vibrator");
        setup_search_views();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        textview.setOnTouchListener(this);
        text_box.requestFocus(0);
        text_box.setSelection(0);
        imm.showSoftInput(text_box, InputMethodManager.SHOW_IMPLICIT);
        CP = new CameraProcess(surface, this, view);
        h.postDelayed(check_text, FRAME_RATE);
    }


    private void resetZoomState() {
        mZoomControl.getZoomState().setPanX(0.5f);
        mZoomControl.getZoomState().setPanY(0.5f);
        mZoomControl.getZoomState().setZoom(1f);
        mZoomControl.getZoomState().notifyObservers();
    }

    public void Blurring(int rad) {
        if (rad < 1) {
            set_img(img.bitmap);
        } else {
            Blurring blur = new Blurring(img.bitmap, rad, this);
            blur.delegate = this;
            blur.execute();
        }
    }


    RelativeLayout list_view;

    private void setup_list_view() {
        list_view = (RelativeLayout) findViewById(R.id.layout_list_view);
        list_view.setVisibility(View.GONE);
        lv = (SingleScrollListView) findViewById(R.id.listView);
        lv.setSingleScroll(true);
        lv.smoothScrollToPosition(1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_view.setVisibility(View.GONE);
                ListAdapter adapter = lv.getAdapter();
                if (adapter.getItem(position) != null) {
                    ImageResult imageInfo = (ImageResult) adapter.getItem(position);
                    mDownload = (DownloadImage) new DownloadImage().execute(imageInfo.getFullUrl());
                }
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 6) {
                    if (view.getLastVisiblePosition() == 1) {
                        view.smoothScrollToPositionFromTop(2, lv.fix_position, 300);
                    } else if (view.getLastVisiblePosition() == totalItemCount - 1) {
                        SA.Search(kw);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });


    }
    //Setup Edit Menu


    public void setup_Blur_SB() {

        blur_SB = (SeekBar) findViewById(R.id.seekBar);
        blur_SB.setMax(15);
        blur_SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Blurring(seekBar.getProgress());
            }
        });
    }

    private void setup_search_views() {
        SV = (SearchView) findViewById(R.id.searchView);
        SV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SA.newSearch(query);
                imm.hideSoftInputFromWindow(text_box.getWindowToken(), 0);
                SV.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void show_toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }


    private void setup_views() {
//        PB = (ProgressBar) findViewById(R.id.progressBar);
        Display display = getWindowManager().getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();
        publish = (ImageView) findViewById(R.id.publish);
        publish.setOnClickListener(this);
        browse = (ImageView) findViewById(R.id.browse);
        browse.setOnClickListener(this);
        random = (ImageView) findViewById(R.id.random);
        random.setOnClickListener(this);
        edit_image = (ImageView) findViewById(R.id.edit_button);
        edit_image.setOnClickListener(this);
        change_font = (ImageView) findViewById(R.id.font);
        change_font.setOnClickListener(this);
        text_layout = (RelativeLayout) findViewById(R.id.text_layout);
        et_background = (ImageView) findViewById(R.id.et_background);
        done = (ImageButton) findViewById(R.id.done_edit_text);
        done.setOnClickListener(this);
        WaitDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_HOLO_DARK);
        WaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        blur_layout = (RelativeLayout) findViewById(R.id.blur_layout);
        guideText = (TextView) findViewById(R.id.guide_text);
        phone = (ImageView) findViewById(R.id.Phone);
        phone.setOnClickListener(this);
        facebook = (ImageView) findViewById(R.id.FB);
        facebook.setOnClickListener(this);
        MTV = (MagicTextView) findViewById(R.id.MTV);
        stroke = (CheckBox) findViewById(R.id.stroke);
        stroke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MTV.setStroke((text_size + DeltaY) / 22, Color.BLACK);
                    isStroke = true;
                    set_text();
                } else {
                    isStroke = false;
                    MTV.setStroke(0, Color.WHITE);
                    set_text();
                }

            }
        });

        select_image_layout = (RelativeLayout) findViewById(R.id.select_image_layout);
        camera_button = (ImageView) findViewById(R.id.camera);
        gallery_button = (ImageView) findViewById(R.id.gallery_button);

        camera_button.setOnClickListener(this);
        gallery_button.setOnClickListener(this);

        textview = (ImageView) findViewById(R.id.textview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.random:
                imm.hideSoftInputFromWindow(text_box.getWindowToken(), 0);
                if (!search) {
                    kw = text_box.getText().toString();
                    SA.Search(kw);
                    isShow_random = !isShow_random;
                    search = true;
                    lv.set_fixed(screen_width, this);
                    list_view.setVisibility(View.VISIBLE);
                } else {
                    if (!kw.equals(text_box.getText().toString())) {
                        Dialog();
                    } else {
                        if (!isShow_random) {
                            list_view.setVisibility(View.VISIBLE);
                            isShow_random = !isShow_random;
                        } else {
                            list_view.setVisibility(View.GONE);
                            isShow_random = !isShow_random;
                        }

                    }
                }
                break;
            case R.id.browse:
                if (isShow_browser) {
                    isShow_browser = false;
                    select_image_layout.setVisibility(View.GONE);
                } else {
                    isShow_browser = true;
                    select_image_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.edit_button:
                if (!isScalingText) {
                    edit_image.setImageResource(R.drawable.edit_pictures);
                    textview.setOnTouchListener(this);
                    blur_layout.setVisibility(View.GONE);
                } else {
                    edit_image.setImageResource(R.drawable.edit_text);
                    textview.setOnTouchListener(null);
                    blur_layout.setVisibility(View.VISIBLE);
                }
                isScalingText = !isScalingText;
                break;
            case R.id.font:
                Typeface tf = cf.fonts.get(font_index).tf;
                MTV.setTypeface(tf);
                font_index++;
                if (font_index == cf.fonts.size()) font_index = 0;
                set_text_to_center();
                set_text();
                break;

            case R.id.done_edit_text:
                isKeyBoardVisible = false;
                update_text();
                Start();
                if (text_to_draw.length() > 0) {
                    get_next_image();
                }
                text_layout.setVisibility(View.GONE);
                break;
            case R.id.camera:
                if (!preview) {
                    CP.start();
                    preview=true;
//                    if (CP.mCamera != null) preview = true;
//                    else{
//                        show_toast("Camera not available");
//                    }
                } else {
                        set_img(CP.take_picture());
                        preview = false;
                }
                break;
            case R.id.gallery_button:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                break;
            case R.id.publish:
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.Phone:
                SaveImage();
                break;
            case R.id.FB:
                share_facebook();
                break;
            default:
                break;
        }
    }

    public void get_next_image() {
        if (text_to_draw.length() > 0) {
            begin = true;
            imm.hideSoftInputFromWindow(text_box.getWindowToken(), 0);
            SA.get_next(text_box.getText().toString());
            WaitDialog.setMessage("Getting Image");
            WaitDialog.setIndeterminate(true);
            WaitDialog.setCanceledOnTouchOutside(true);
            WaitDialog.show();
            wait_search.postDelayed(wait, 100);
        }
    }

    Handler wait_search = new Handler();
    private Runnable wait = new Runnable() {
        @Override
        public void run() {
            if (SA.imageResults.size() < SA.position + 1) {
                if(SA.isConnectInternet)
                wait_search.postDelayed(wait, 100);
                else{
                    WaitDialog.dismiss();
                }
            } else {
                mDownload = (DownloadImage) new DownloadImage().execute(SA.imageResults.get(SA.position).getFullUrl());
            }
        }
    };

    public void get_previous_image() {
        SA.position -= 1;
        mDownload = (DownloadImage) new DownloadImage().execute(SA.imageResults.get(SA.position).getFullUrl());
    }

    int font_index = 0;

    public void set_img(Bitmap bm) {
        if (bm != null) {
            blur_SB.setProgress(0);
            img.bitmap = bm;
            img.x = viewWidth / 2 - img.width() / 2;
            isChangedImage = true;
            view.setImage(img.bitmap);
        }
    }


    public void Start() {
        viewWidth = view.getWidth();
        viewHeight = view.getHeight();
        text_size = viewHeight / 10;
        MTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size);
        MTV.setStroke(0, Color.WHITE);
        MTV.addOuterShadow(5, 2, 2, Color.BLACK);
        set_text_to_center();


        set_text();
    }

    private Runnable check_text = new Runnable() {
        @Override
        public void run() {
            if (text_box.getText().length() == 0) {
                guideText.setVisibility(View.VISIBLE);
            } else {
                guideText.setVisibility(View.GONE);
            }
            if (isKeyBoardVisible) h.postDelayed(check_text, FRAME_RATE);
        }
    };

    public void set_text_to_center() {
        text_y = viewHeight/4;
        text_x = 0;
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(text_x, text_y);
        textview.setScaleType(ImageView.ScaleType.MATRIX);
        textview.setImageMatrix(matrix);
    }

    public void check_text_position() {
        int maxy = viewHeight - 150;
        if (text_y < 0) text_y = 0;
        else if (text_y > maxy) text_y = maxy;

        int maxx = viewWidth / 2;
        if (text_x < -viewWidth / 2) text_x = -viewWidth / 2;
        else if (text_x > maxx) text_x = maxx;

    }

    String text_to_draw = "";

    public void update_text() {
        String temp = text_box.getText().toString();
        if (!text_to_draw.equals(temp)) {
            MTV.setText(temp);
            text_to_draw = temp;
        }
    }

    float txt_initialX = 0, txt_initialY = 0, txt_offsetX = 0, txt_offsetY = 0;
    int DeltaY, DeltaX;

    private enum Mode {
        UNDEFINED, PAN, ZOOM
    }

    Mode mMode;
    private final Runnable mLongPressRunnable = new Runnable() {
        public void run() {
            mMode = Mode.ZOOM;
            mVibrator.vibrate(50);

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        tapDetector.onTouchEvent(event);
        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.postDelayed(mLongPressRunnable, mLongPressTimeout);
                txt_offsetX = event.getX();
                txt_offsetY = event.getY();
                txt_initialX = text_x;
                txt_initialY = text_y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == Mode.ZOOM) {
                    DeltaY += (int) (txt_offsetY - event.getY()) / 2;
                    DeltaY = Math.max(0, DeltaY);
                    int size = text_size + DeltaY;
                    MTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    if (isStroke) MTV.setStroke(size / 22, Color.BLACK);
                    set_text();
                } else if (mMode == Mode.PAN) {
                    text_y = (int) (txt_initialY + event.getY() - txt_offsetY);
                    text_x = (int) (txt_initialX + event.getX() - txt_offsetX);
                    Matrix matrix = new Matrix();
                    matrix.reset();
                    check_text_position();
                    matrix.postTranslate(text_x, text_y);
                    textview.setScaleType(ImageView.ScaleType.MATRIX);
                    textview.setImageMatrix(matrix);
                } else {
                    final float scrollX = txt_offsetX - x;
                    final float scrollY = txt_offsetY - y;
                    final float dist = (float) Math.sqrt(scrollX * scrollX + scrollY * scrollY);
                    if (dist >= mScaledTouchSlop) {
                        v.removeCallbacks(mLongPressRunnable);
                        mMode = Mode.PAN;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mMode = Mode.UNDEFINED;
                break;
        }
        return true;
    }

    @Override
    public void processFinish(Bitmap output) {
        img.blurred = output;
        view.setImage(img.blurred);
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private ProgressDialog simpleWaitDialog;
        boolean parent_dialog = false;

        @Override
        protected Bitmap doInBackground(String... urls) {
            return getBitmapFromURL(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
            if (!WaitDialog.isShowing()) {
                parent_dialog = false;
                simpleWaitDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_HOLO_DARK);
                simpleWaitDialog.setMessage("Downloading Image");
                simpleWaitDialog.show();
                simpleWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDownload.cancel(true);
                    }
                });
            } else {
                parent_dialog = true;
            }
        }


        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                set_img(result);
//                draw();

            } else {
                get_next_image();
            }
            if (parent_dialog) WaitDialog.dismiss();
            else simpleWaitDialog.dismiss();
//            surface.requestFocus();
        }

        @Override
        protected void onCancelled() {
            show_toast("Cancel Download");
        }
    }

    private void set_text() {
        MTV.setDrawingCacheEnabled(false);
        MTV.setDrawingCacheEnabled(true);
        MTV.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(MTV.getDrawingCache());
        textview.setImageBitmap(b);
    }

    private void Dialog() {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Do you want to search images again?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        lv.setAdapter(null);
                        SA = new SearchActivity(MainActivity.this, lv);
                        kw = text_box.getText().toString();
                        SA.Search(kw);
                        isShow_random = !isShow_random;
                        search = true;
                        list_view.setVisibility(View.VISIBLE);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        list_view.setVisibility(View.VISIBLE);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public class TapGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if (isChangedImage) {
                Bitmap temp = IP.Resize(img.bitmap, 100);
                temp = IP.fastblur(temp, 3);
                et_background.setImageBitmap(temp);
                isChangedImage = false;
            }
            text_layout.setVisibility(View.VISIBLE);
            text_layout.setVisibility(View.INVISIBLE);
            text_layout.setVisibility(View.VISIBLE);
            imm.showSoftInput(text_box, 0);
            isKeyBoardVisible = true;
            text_box.requestFocus(text_to_draw.length() - 1);
            text_box.setSelection(text_box.getText().length());
            text_box.setHint("");
            h.postDelayed(check_text, FRAME_RATE);
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            set_img(BitmapFactory.decodeFile(picturePath));
//            draw();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        }

    }

    File file;

    private void SaveImage() {
        WaitDialog.setMessage("Wait");
        WaitDialog.setIndeterminate(true);
        WaitDialog.setCanceledOnTouchOutside(true);
        WaitDialog.show();
        String filename;
        Date date = new Date(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = sdf.format(date) + ".jpg";

        File path = new File(Environment.getExternalStorageDirectory() + "/mQuote/");

        if (!path.exists()) {
            path.mkdir();
        }

        file = new File(path.getAbsolutePath(), filename);


        try {
            FileOutputStream fos = new FileOutputStream(file);
            surface.setDrawingCacheEnabled(true);
            surface.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(surface.getDrawingCache());
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver()
                    , file.getAbsolutePath(), file.getName(), file.getName());
            show_toast("Saved");
            MTV.setDrawingCacheEnabled(false);
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WaitDialog.dismiss();
    }

    private void share_facebook() {
        WaitDialog.setMessage("Wait");
        WaitDialog.setIndeterminate(true);
        WaitDialog.setCanceledOnTouchOutside(true);
        WaitDialog.show();
        SaveImage();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));


        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager()
                .queryIntentActivities(share, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase()
                    .startsWith("com.facebook.katana")) {
                share.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            show_toast("Not Found Facebook Apps");
        } else {
            startActivity(share);
        }
        WaitDialog.dismiss();
    }
}
