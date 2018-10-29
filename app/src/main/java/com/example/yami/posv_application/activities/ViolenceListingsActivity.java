package com.example.yami.posv_application.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.adapters.ViolenceAdapter;
import com.example.yami.posv_application.models.Violence;
import com.example.yami.posv_application.utilities.CustomTypefaceSpan;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ViolenceListingsActivity extends BaseActivity {

    private ListView listingsView;
    private List<Violence> examples;
    private Context context;

    ExpandableRelativeLayout expandableLayout1,expandableLayout2,expandableLayout3,expandableLayout4,
            expandableLayout5,expandableLayout6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflate the activity & perform app styling
        setContentView(R.layout.activity_violence_listings);
        setupActionBarTheme();
        this.context = this;

        // 2. Load the Bakery data
        loadExamples();

        // 3. Initialize the Bakery adapter
        ViolenceAdapter adapter = new ViolenceAdapter(this, R.layout.list_item_violence, examples);

        // 4. Inflate our ListView and set the adapter on it
        listingsView = (ListView)findViewById(R.id.listings_view);
        listingsView.setAdapter(adapter);

        // 5. Handle the click
        listingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView listView = (ListView) view.getParent();
                if (listView != null) {

                    Violence violence = examples.get(position);
                    if (violence != null) {

                        //Toast.makeText(context, "Clicked on " + violence.newsSubject, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Loads examples data into List<Violence>
    private void loadExamples() {

        examples = new ArrayList<>();

        Bitmap example1Icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_filter_1_black_24dp);
        Bitmap example2Icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap example3Icon =BitmapFactory.decodeResource(getResources(), R.mipmap.test1);

        Violence example1 = new Violence("고등학교 동아리 선배 집단 폭행 및 \n학대 사건", " A 고등학교 학생이 동아리 선배들로부터 폭행과 성적학대를 당했다.\n\n" +
                "A 고등학교 2학년 김모(17)군은 지난 해 3월부터 11월까지 주먹밥을 사오지 않는다는 이유로 선배 4명으로부터 폭행과 성추행을 당했다고 주장해 부모가 지난 달 30일 3학년 학생 4명을 김천경찰서에 고소했다. A군측에 의하면 선배들은 각각 1~2명씩 별도로 A군이 동작이 느리다는 등의 이유로 때리거나 코에 담배를 끼워 피우게 하는 등 괴롭히고 돈을 빼앗기도 했다고 한다. 또 일부 성추행 사실을 언급했다. 새학기에 들어 A군이 등교를 하지 않자 부모가 이러한 사실을 확인하고 학교에 알리게 되었다.\n\n" +
                "경찰은 조사 중 선배 학생들이 동아리 활동을 하며 1~2명씩 별도로 모두 4명이 A군을 때리고 괴롭힌 것으로 예상하였다. 성추행 여부는 진위를 확인중이며, 혐의가 드러나면 사법 처리 할 예정임을 밝혔다.\n" +
                "학교측은 이에 지난 (2016년) 3월 29일 학교폭력자치위원회를 열어 가해 학생 4명에 대해 출석정지 10일과 사회봉사 결정을 내렸다.", "2015-03-29", "김천");
        example1.logo = example1Icon;
        examples.add(example1);

        Violence example2 = new Violence("빵셔틀·폭행사건", " A군(당시 16세)은 2012년 4월부터 9개월 동안 같은반 학생 B군 등 5명에게 매점에 빵과 음료수를 사오라며 100여 차례에 걸쳐 폭행과 협박을 당했다. \n\n"+
                "같은 해 9월에는 B군에게 휴대전화를 빼앗겼고 B군은 휴대전화를 가져가 약 50만원의 요금이 나오게 사용한 뒤 장물업자에게 기기를 팔기도 했다. \n" +
                "또한 B군 등 2명은 A군을 주먹으로 때린 뒤 바지를 벗으라고 강요했고, 휴대전화로 A군의 성기를 촬영했다. 이에 A군에게 “돈을 주면 동영상을 삭제해주겠다”고 협박해 6000원을 빼앗았다. \n"+
                "A군은 학교폭력 가해자를 고소했고, 광주시 교육청과 학교 측을 상대로 1억원대 손해배상 소송을 제기했다", "2013-01-01", "광주");
        example2.logo = example2Icon;
        examples.add(example2);

        Violence example3 = new Violence("자폐아 초등학생 폭행사건", " 서울 강남 A초등학교에서 자폐 아동이 친구들에게 학교 폭력을 당하는 사건이 발생했다. 가해 학생들은 학교 동급생 2명이었으며, 피해 학생은 아스퍼거 증후군을 앓고 있었다. \n\n"+
                "피해 학생은 5월 11, 13일 이틀에 걸쳐 학교에서 가해 학생들에게 ‘체포놀이’를 하던 중 폭행을 당했다고 진술했으며, 이 사실을 어른들에게 털어놨다는 이유로 정강이를 걷어차고 화장실에 고립시킨 뒤 바지 속에 손을 넣어 성기 부분을 잡아 뜯기며 보복폭행을 당했다고 주장했다. \n\n"+
                "피해 학생은 유치원생 때부터 친구로 지낸 학생 등 동급생 2명과 체포놀이를 했다. 항상 피해학생이 범인 역할을 맡았으며 경찰인 동급생들에게 체포될 때마다 꼬집거나 발로 차는 등의 폭행을 당했다. \n"+
                "이와 관련하여 학교 측에서는 몇 차례 학교폭력대책자치위원회(학복위)를 연 끝에 가해자로 지목된 학생들이 피해 학생을 괴롭힌 사실은 인정하면서도 성적 학대는 증거와 증인이 없다고 하여 가해 학생들에게 그 해 종업식 때까지 접촉과 보복을 금지하도록 하고 학부모와 함께 2시간씩 특별교육을 받도록 하였다.\n\n" +
                " 하지만 피해자의 부모는 징계가 너무 가볍다고 생각하여 강제전학 조치를 내려달라는 재심을 청구했다. 또한 5월 18일에 경찰에 고소장을 제출했지만 형법상 처벌이 불가능한 만 14세 미만의 미성년자(촉법소년)라는 이유로 사건을 각하 의견으로 검찰에 송치했다. \n"+
                "인터넷 블로그에 아들이 학교 친구들에게 학교 폭력을 당했다며 가해자 처벌과 학교 측의 재발 방지를 촉구하는 서명운동에 동참해달라는 글을 게시했다. 블로그 글에는 허벅지와 종아리에 멍이 들고 성기 부분에서 출혈이 일어난 피해 학생의 사진이 여러 장 첨부돼 있었다. \n"+
                "하지만 가해자로 지목된 학생의 어머니는 학교 폭력 사실에 대해 부인하는 글을 올리면서 논란이 증폭되었다.", "2015-05-13", "서울");

        examples.add(example3);

        Violence example4 = new Violence("자살 놀이 사건", " 최근 강릉의 한 중학교에서  2학년생 6명이 A군 포함 1학년생 5명을 불러 가위바위보를 시켰다. \n"+
                "겁에 질린 1학년생들은 강제로 기절놀이를 했고 2학년생 중 한 명이 가위바위보에서 진 A군의 목을 세게 졸랐다. 순간적으로 숨이 막혀 기절한 A 군은 당시 충격에서 헤어 나오지 못해 정신과 치료를 받고 있다.",
                "2016-07-07", "강릉");

        examples.add(example4);



    }

    // Sets the app title to use a custom font
    private void setupActionBarTheme() {

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lobster.ttf");
        SpannableStringBuilder spannedTitlte = new SpannableStringBuilder(this.getTitle());
        spannedTitlte.setSpan(new CustomTypefaceSpan("", font), 0, spannedTitlte.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(spannedTitlte);
        }
    }


    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        finish();
    }
}