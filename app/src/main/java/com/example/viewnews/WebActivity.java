package com.example.viewnews;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.viewnews.tools.BaseActivity;
import com.example.viewnews.usermodel.AdapterComment;
import com.example.viewnews.usermodel.Comment;
import com.example.viewnews.usermodel.UserInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
//若需要启用Javascript，则抑制其警告
@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends BaseActivity implements View.OnClickListener{

    private WebView webView;
    private Toolbar navToolbar, commentToolBar;
    private RelativeLayout rl_comment;
    private LinearLayout rl_enroll;
    private String urlData, pageUniquekey, pageTtile;
    private ImageView chat,comment;
    private TextView hide_down;
    private EditText comment_content;
    private Button comment_send;
    private ListView comment_list;
    private RecyclerView recycler_list;
    private AdapterComment adapterComment;
    private List<Comment> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        navToolbar = (Toolbar) findViewById(R.id.toolbar_webView);
//        commentToolBar = (Toolbar) findViewById(R.id.toolbar_webComment);
//        //将底部评论框的toolbar放在主界面上
//       findViewById(R.id.toolbar_webComment).bringToFront();

        rl_comment=findViewById(R.id.rl_comment);
        rl_enroll=findViewById(R.id.rl_enroll);
        chat=findViewById(R.id.chat);
        comment=findViewById(R.id.comment);
        hide_down=findViewById(R.id.hide_down);
        comment_content=findViewById(R.id.comment_content);
        comment_send=findViewById(R.id.comment_send);
        comment_list=findViewById(R.id.comment_list);
        //recycler_list=findViewById(R.id.recycler_view);
        data = new ArrayList<>();
        // 初始化适配器
        adapterComment = new AdapterComment(getApplicationContext(), data);
        // 为评论列表设置适配器
        comment_list.setAdapter(adapterComment);
       // recycler_list.setAdapter(adapterComment);
    }

    //活动由不可见变为可见时调用
    @Override
    protected void onStart() {
        super.onStart();
        // 获取html页面的连接
        urlData = getIntent().getStringExtra("pageUrl");
        pageUniquekey = getIntent().getStringExtra("uniquekey");
        pageTtile = getIntent().getStringExtra("news_title");

        System.out.println("当前新闻id为：" + pageUniquekey);
        System.out.println("当前新闻标题为：" + pageTtile);

        // 通过WebView中的getSettings方法获得一个WebSettings对象
        WebSettings settings = webView.getSettings();

        // 详细讲解：https://www.jianshu.com/p/0d7d429bd216
        // 开启javascript：h5页要一般都有js,设置为true才允许h5页面执行js，但开启js非常耗内存，经常会导致oom，
        // 为了解决这个问题，可以在onStart方法中开启，在onStop中关闭。
        settings.setJavaScriptEnabled(true);

        //设置支持缩放
        settings.setSupportZoom(true);
        // 设置出现缩放工具。若为false，则该WebView不可缩放
        settings.setBuiltInZoomControls(true);
        // 设置允许js弹出alert对话框
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 设置webview推荐使用的窗口，使html界面自适应屏幕
        // 原因讲解：https://blog.csdn.net/SCHOLAR_II/article/details/80614486
        settings.setUseWideViewPort(true);
        // 设置WebView底层的布局算法，参考LayoutAlgorithm#NARROW_COLUMNS，将会重新生成WebView布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 设置缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        // 隐藏webview缩放按钮
        settings.setDisplayZoomControls(false);
        // 加载网页连接
        webView.loadUrl(urlData);


        // Toolbar通过setSupportActionBar(toolbar) 被修饰成了actionbar。
        //setSupportActionBar(commentToolBar);

        // 设置菜单栏标题
        navToolbar.setTitle("北化新闻");
        setSupportActionBar(navToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置返回图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
//        commentToolBar.inflateMenu(R.menu.tool_webbottom);
//        commentToolBar.setTitle("hhh");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 页面开始加载时就去查看收藏表中是否有对应的记录，组合键（账号和新闻号）
                List<NewsCollectBean> beanList = LitePal.where("userIdNumer = ? AND newsId = ?", MainActivity.currentUserId == null ? "" : MainActivity.currentUserId, pageUniquekey).find(NewsCollectBean.class);
                // 获取收藏按钮
               // MenuItem u = commentToolBar.getMenu().getItem(1);
                if(beanList.size() > 0) {
                  //  u.setIcon(R.drawable.ic_star_border_favourite_yes);
                    chat.setImageResource(R.drawable.ic_star_border_favourite_yes);
                } else {
                 //   u.setIcon(R.drawable.ic_star_border_favourite_no);
                    chat.setImageResource(R.drawable.ic_star_border_favourite_no);
                }
            }




            // 重写此方法可以让webView处理https请求。
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 默认的处理方式，WebView变成空白页
                // handler.cancel();
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
            }
        });
        showcomment();
        setListener();

    }//end onstart

    public void showcomment()
    {
        List<Comment> pp=LitePal.where("uniquekey=?",pageUniquekey).find(Comment.class);
        if(pp.size()>0)
        {
           // comment_list.setVisibility(View.VISIBLE);
        }
        for(Comment p:pp)
        {
            adapterComment.addComment(p);
        }
    }
    // 活动不可见时关闭脚本，否则可能会导致oom
    @Override
    protected void onStop() {
        super.onStop();
        webView.getSettings().setJavaScriptEnabled(false);
    }

     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 左上角的id
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                // 结束当前活动
                WebActivity.this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void setListener(){
        comment.setOnClickListener(this);
        chat.setOnClickListener(this);
        hide_down.setOnClickListener(this);
        comment_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:

                // 显示评论框
                rl_enroll.setVisibility(View.GONE);
                rl_comment.setVisibility(View.VISIBLE);
                comment_list.setVisibility(View.VISIBLE);
                break;
            case R.id.comment_content:
                // 弹出输入法
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.chat:
                //下一步实现点击收藏功能，以及用户查看收藏功能
                if(!TextUtils.isEmpty(MainActivity.currentUserId))
                {
                    // 先去查询一下是否有收藏过，然后加载每条新闻的时候查看是否已经被收藏，若被收藏，则将收藏按钮背景色设置为红色，否则为白色

                    List<NewsCollectBean> bean = LitePal.where("userIdNumer = ? AND newsId = ?", MainActivity.currentUserId, pageUniquekey).find(NewsCollectBean.class);
                    NewsCollectBean currentNews = null;
                    System.out.println(bean);
                    String answer = "";
                    if(bean.size() > 0)
                    {
                        System.out.println("111111111111111");
                        int i = LitePal.deleteAll(NewsCollectBean.class, "userIdNumer = ? AND newsId = ?", MainActivity.currentUserId, pageUniquekey);
                        if(i > 0)
                        {
                            answer = "取消收藏！";
                           chat.setImageResource(R.drawable.ic_star_border_favourite_no);
                        }
                        else answer = "取消失败！";
                        // System.out.println("6666666666666666");
                    }
                    else {
                        currentNews = new NewsCollectBean();
                        currentNews.setUserIdNumer(MainActivity.currentUserId);
                        currentNews.setNewSTitle(pageTtile);
                        currentNews.setNewsId(pageUniquekey);
                        currentNews.setNewsUrl(urlData);
                        boolean isSave = currentNews.save();
                        System.out.println("收藏的新闻：" + currentNews);
                        if(isSave)
                        {
                            answer = "收藏成功！";
                           chat.setImageResource(R.drawable.ic_star_border_favourite_yes);
                        }
                        else answer = "收藏失败！";
                    }
                    Toast.makeText(WebActivity.this , answer, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(WebActivity.this, "请先登录后再收藏！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.hide_down:
                // 隐藏评论框
                rl_enroll.setVisibility(View.VISIBLE);
                rl_comment.setVisibility(View.GONE);
                comment_list.setVisibility(View.GONE);
                // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                InputMethodManager im = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);
                break;
            case R.id.comment_send:
                // 发送
                if(!TextUtils.isEmpty(MainActivity.currentUserId)) {
                    rl_enroll.setVisibility(View.GONE);
                    rl_comment.setVisibility(View.VISIBLE);
                    comment_list.setVisibility(View.VISIBLE);
                    // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                    InputMethodManager immm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);
                    sendComment();
                }
                else{
                    Toast.makeText(WebActivity.this, "请先登录后再评论！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送评论
     */
    public void sendComment(){
        if(comment_content.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "评论不能为空！", Toast.LENGTH_SHORT).show();
        }else{
            List<UserInfo> nickname=LitePal.select("nickName").where("userAccount = ?",MainActivity.currentUserId).find(UserInfo.class);
            // 生成评论数据
            //System.out.println(nickname.get(0).getNickName());
            Comment comment = new Comment();
            comment.setName(nickname.get(0).getNickName());
            comment.setContent(comment_content.getText().toString());
            comment.setUniquekey(pageUniquekey);
            comment.save();
            adapterComment.addComment(comment);
            // 发送完，清空输入框
            comment_content.setText("");

            Toast.makeText(getApplicationContext(), "评论成功！", Toast.LENGTH_SHORT).show();
        }
    }
}
