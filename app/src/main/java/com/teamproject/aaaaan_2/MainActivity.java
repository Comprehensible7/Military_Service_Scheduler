package com.teamproject.aaaaan_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;
import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.util.DialogSampleUtil;
import com.teamproject.aaaaan_2.login.LoginActivity;
import com.teamproject.aaaaan_2.ui.menu.DdayFragment;
import com.teamproject.aaaaan_2.ui.menu.FirstScreenFragment;
import com.teamproject.aaaaan_2.ui.menu.MiniGameFragment;
import com.teamproject.aaaaan_2.ui.menu.MyPageFragment;
import com.teamproject.aaaaan_2.ui.menu.PersonListFragment;
import com.teamproject.aaaaan_2.ui.menu.ScheduleFragment;
import com.teamproject.aaaaan_2.ui.menu.ScheduleFragmentSDK21;
import com.teamproject.aaaaan_2.util.FragmentCallBack;
import com.teamproject.aaaaan_2.util.LoginSharedPreference;
import com.teamproject.calendarviewlib.CalendarView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hanman-yong on 2019-12-30.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallBack {
    private final String TAG = "D-Day MainAct";

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private File tempFile;

    private CalendarView mCalendarView;

    public long time = 0;
    public static Context mContext;

    dbHandler handler;

    Toolbar toolbar;

    CircleImageView profile_image;
    TextView profile_id_text;

    Fragment fragment;

    Fragment first_screenFragment;
    Fragment scheduleFragment_sdk21;
    Fragment scheduleFragment;
    Fragment d_dayFragment;
    Fragment person_listFragment;
    Fragment mini_gameFragment;
    Fragment my_pageFragment;

    FloatingActionButton fab;

    String my_id = "";
    String mCurrentPhotoPath;

    boolean bFirst = false;
    Bitmap img_bitmap;

    Uri imageURI, albumURI;
    byte[] img_bit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        tedPermission();

        my_id = LoginSharedPreference.getAttribute(this, LoginActivity.LOGIN_ID);

        if (handler == null) {
            handler = dbHandler.open(this);
        }

        // 상단 툴바 설정
        toolbar = findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);

        // 우측 하단 플로팅버튼 설정
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, first_screenFragment).commit();
                Snackbar.make(view,"기존 화면으로 돌아가려면 뒤로가기를 눌러주세요.",Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                bFirst = true;
                DialogSampleUtil.hideKeypad(mContext, view);
            }
        });

        // 전체 화면 설정
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 네비게이션 화면 설정
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // 리스너 설정

        // 프래그먼트 생성
        first_screenFragment = new FirstScreenFragment();
        scheduleFragment = new ScheduleFragment();
        scheduleFragment_sdk21 = new ScheduleFragmentSDK21();
        d_dayFragment = new DdayFragment();
        person_listFragment = new PersonListFragment();
        mini_gameFragment = new MiniGameFragment();
        my_pageFragment = new MyPageFragment();

        // 첫번째 뜰 화면 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, first_screenFragment).commit();

        // 파이어베이스 푸시메시지 받기위한 설정
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // FCM 토큰을 token 변수에 저장하여 로그로 출력.
                        String token = task.getResult().getToken();
                        Log.d(TAG, "FCM 토큰: " + token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("second");

        View header = navigationView.getHeaderView(0);
        profile_image = (CircleImageView) header.findViewById(R.id.main_profile_image);
        profile_id_text = (TextView) header.findViewById(R.id.main_profile_id_text);

        profile_id_text.setText(my_id + " 님 접속을 환영합니다.");

        try {
            img_bit = handler.image_parsing_select(my_id);

            if (img_bit.length != 0) {
                img_bitmap = BitmapFactory.decodeByteArray(img_bit, 0, img_bit.length);

                profile_image.setImageBitmap(img_bitmap);
                onLog();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate)catch 이미지 불러오기 실패.");
        }
    }

    @Override
    public void onBackPressed() {
        // 네비게이션 메뉴 취소버튼으로 닫기.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (bFirst) {
            if (fragment == null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            bFirst = false;
            return;
        }

        // 취소버튼 두번 눌러야 종료.
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            moveTaskToBack(true);
            finish();
            ActivityCompat.finishAffinity(this);

            // 이놈 넣으면 SharedPreference 정보 날아가는듯.. 그래서 이건 안됨.
            //android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(fragment == scheduleFragment_sdk21) {
            getMenuInflater().inflate(R.menu.menu_toolbar_calendar_view, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCalendarView = findViewById(R.id.calendarView);

        switch (item.getItemId()) {
            case R.id.action_today: {
                mCalendarView.setSelectedDate(Calendar.getInstance());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            onChangedFragment(1, null);
        } else if (id == R.id.nav_d_day) {
            onChangedFragment(2, null);
        } else if (id == R.id.nav_list) {
            onChangedFragment(3, null);
        } else if (id == R.id.nav_mini_game) {
            onChangedFragment(4, null);
        } else if (id == R.id.nav_my_page) {
            onChangedFragment(5, null);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChangedFragment(int position, Bundle bundle) {
        if (position == 4) {
            fab.hide();
        } else {
            fab.show();
        }
        switch (position) {
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fragment = scheduleFragment_sdk21;
                } else {
                    fragment = scheduleFragment;
                }
                toolbar.setTitle("달력");
                break;
            case 2:
                fragment = d_dayFragment;
                toolbar.setTitle("D-Day 등록");
                break;
            case 3:
                fragment = person_listFragment;
                toolbar.setTitle("비상연락망");
                break;
            case 4:
                fragment = mini_gameFragment;
                toolbar.setTitle("미니게임");
                break;
            case 5:
                fragment = my_pageFragment;
                toolbar.setTitle("마이 페이지");
                break;
            default:
                break;
        }

        // 선택한 프래그먼트로 전환
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

    }

    // 로그아웃 버튼 클릭시 로그인 화면으로 이동
    public void onLogOut(View view) {
        // 현재 로그인한 정보를 지운다.
        LoginSharedPreference.removeAttribute(MainActivity.this, LoginActivity.LOGIN_ID);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 이미지를 클릭하면 팝업 메뉴가 뜬다.
    public void changeImage(View view) {
        if (!isPermission) {
            Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
        } else {

            PopupMenu pop = new PopupMenu(getApplicationContext(), view);
            getMenuInflater().inflate(R.menu.main_menu, pop.getMenu());

            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.one:
                            // 실제 카메라 구동 코드는 함수로 처리
                            captureCamera();
                            break;
                        case R.id.two:
                            //갤러리에 관한 권한을 받아오는 코드
                            getAlbum();
                            break;
                        case R.id.three:
                            //기본이미지
                            profile_image.setImageResource(R.mipmap.user);
                            imageDelete();
                            break;
                    }
                    return true;
                }
            });
            pop.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                try {
                    Log.i("REQUEST_TAKE_PHOTO", "OK!!!!!!");
                    galleryAddPic();

                    profile_image.setImageURI(imageURI);
                } catch (Exception e) {
                    Log.e("REQUEST_TAKE_PHOTO", e.toString());
                }

                break;
            case REQUEST_IMAGE_CROP:
                profile_image.setImageURI(albumURI);
                break;

            case PICK_FROM_ALBUM: {

                Uri photoUri = data.getData();
                Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

                cropImage(photoUri);

                break;
            }
            case Crop.REQUEST_CROP: {
                setImage();
            }
        }
    }

    // 앨범 실행
    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 카메라 실행해서 사진 받기
    private void captureCamera() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    tempFile = createImageFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tempFile != null) {
                    Uri providerUri = FileProvider.getUriForFile(this, getPackageName(), tempFile);
                    imageURI = providerUri;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri);
                    startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
                }
            } else {
                Toast.makeText(this, "접근 불가능 합니다", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    // 사진 파일 만들기
    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, imageFileName);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        Log.d(TAG, "createImageFile : " + mCurrentPhotoPath);

        return imageFile;
    }

    /**
     * Crop 기능
     */
    private void cropImage(Uri photoUri) {

        Log.d(TAG, "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if (tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (Exception e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }

    // 앨범에서 가져온걸 이미지뷰에 넣어주기.
    private void setImage() {

        // 이미지의 각도롤 정방향으로 돌려준다.
        BitmapFactory.Options options = new BitmapFactory.Options();

        img_bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        // 이미 등록된 이미지가 있는지 검색해본후에 있다면 해당 행 삭제.
        imageDelete();

        // sqlite에 등록하기 위해 바이트배열로 변환 후 디비에 저장
        getByteArrayFromBitmap(img_bitmap);

        // 이미지 등록
        profile_image.setImageBitmap(img_bitmap);

        onLog();

        // 이미지를 넣은 후 임시로 사진파일을 갖고있던 변수를 초기화해준다.
        tempFile = null;
    }


    // 갤러리에 사진 추가하기
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);

        BitmapFactory.Options options = new BitmapFactory.Options();

        int degree = getImageOrientation(mCurrentPhotoPath);

        img_bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        Log.d(TAG, "setImage : " + file.getAbsolutePath());

        img_bitmap = imgRotate(img_bitmap, degree);

        imageDelete();

        getByteArrayFromBitmap(img_bitmap);

        Uri contentURI = Uri.fromFile(file);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // sqlite의 blob 자료형은 바이트배열이다. 때문에 비트맵을 바로 넣으면 안되고, 바이트배열로 변환해서 넣어줘야한다.
    // 이미지 비트맵 파일을 바이트 배열로 바꿔서 DB에 넣어준다.
    public void getByteArrayFromBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();

            handler.member_image_insert(data, my_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 이미지의 현재 각도 얻어오기.
    public static int getImageOrientation(String path) {

        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int rot = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (rot == ExifInterface.ORIENTATION_ROTATE_90) {
                rotation = 90;
            } else if (rot == ExifInterface.ORIENTATION_ROTATE_180) {
                rotation = 180;
            } else if (rot == ExifInterface.ORIENTATION_ROTATE_270) {
                rotation = 270;
            } else {
                rotation = 0;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rotation;
    }

    // 이미지 회전
    public static Bitmap imgRotate(Bitmap bmp, int orientation) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }


    // 퍼미션 물어보기. (카메라, 저장소 접근)
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    // 이미지를 변경할 경우에 기존 이미지를 지우는 과정
    public void imageDelete() {
        try {
            img_bit = handler.image_parsing_select(my_id);

            // 검색쿼리를 통해 문자열값이 존재한다면 삭제쿼리 진행.
            if (img_bit.length != 0) {
                handler.member_image_delete(my_id);
                //sql_db.execSQL("DELETE FROM member_image where img_id = '" + my_id + "';");
            }
        } catch (Exception e) {
            Log.e(TAG, "imageDelete " + e);
        }
    }

    // 그냥 내가 보고싶어서 로그 띄움.
    public void onLog() {
        try {
            img_bit = handler.image_parsing_select(my_id);

            // 안해도 되는짓이지만 String 형으로 바꿔서 바이트 출력해봄;
            String image_temp = Base64.encodeToString(img_bit, Base64.DEFAULT);
            Log.d(TAG, "onLog " + my_id + " / " + image_temp);

        } catch (Exception e) {
            Log.e(TAG, "onLog " + e);
        }
    }


}
