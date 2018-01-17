package sp.phone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.TopicHistoryManager;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2018/1/17.
 */

public class TopicHistoryFragment extends BaseFragment implements View.OnClickListener {

    private TopicListAdapter mTopicListAdapter;

    private RecyclerViewEx mListView;

    private TopicHistoryManager mTopicHistoryManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mTopicHistoryManager = TopicHistoryManager.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable FrameLayout container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTopicListAdapter = new TopicListAdapter(getContext());
        mTopicListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        mListView.setAdapter(mTopicListAdapter);

        super.onViewCreated(view, savedInstanceState);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0) {
                    mTopicHistoryManager.removeTopicHistory(position);
                    mTopicListAdapter.remove(position);
                }

            }
        });
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mListView);
        setData(mTopicHistoryManager.getTopicHistoryList());
    }

    private void setData(List<ThreadPageInfo> topicLIst) {
        TopicListInfo listInfo = new TopicListInfo();
        listInfo.setThreadPageList(topicLIst);
        mTopicListAdapter.setData(listInfo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_black_list_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_all) {
            mTopicHistoryManager.removeAllTopicHistory();
            mTopicListAdapter.removeAll();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        ThreadPageInfo info = (ThreadPageInfo) view.getTag();
        ArticleListParam param = new ArticleListParam();
        param.tid = info.getTid();
        param.page = info.getPage();
        param.title = info.getSubject();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, param);
        intent.putExtras(bundle);
        intent.setClass(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
        startActivity(intent);
    }
}