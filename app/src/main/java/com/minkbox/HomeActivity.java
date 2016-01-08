package com.minkbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.DialogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {
    Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    public static HomeActivity homeActivityInstance = null;
    List<Category> catogaryList = new ArrayList<Category>();
    ArrayList<String> category = new ArrayList<String>();
    DialogManager alert = new DialogManager();
    Button cancle_category;
    Button apply_category;
    DataBaseHelper db;
    Button cancle;
    PopupWindow pwindo;
    public static String ckeckBoxChildStatus = "";
    public static CheckBox allCategoryCheckbox;
    private ArrayList<String> mData;
    private ArrayList<String> mtitle;
    AllCategoryArrayAdapter allCategoryListAdapter;
    AllCategoryModel[] allCategoryModelArray;
    ListView lv;
    TextView alerText, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        homeActivityInstance = this;
        mData = new ArrayList<String>();
        mtitle = new ArrayList<String>();
        db = new DataBaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        getSupportActionBar().setTitle(title);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        displayView(7);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                if(AppPreferences.getId(HomeActivity.this).equalsIgnoreCase("")){
                    Intent i1 = new Intent(HomeActivity.this, UserRegisterLoginActivity.class);
                    i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i1);
                }else {
                    Intent i1 = new Intent(HomeActivity.this, Conversations.class);
                    startActivity(i1);
                }
                break;
            case 1:
                if(!AppPreferences.getId(HomeActivity.this).equalsIgnoreCase(""))
                {
                    Intent in = new Intent(HomeActivity.this, UserNotification.class);
                    startActivity(in);
                }else
                {
                    Toast.makeText(getApplicationContext(), " You have to first login in app", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                Intent inte = new Intent(HomeActivity.this, CollectionActivity.class);
                startActivity(inte);
                break;
            case 4:
                AppPreferences.setNewInYourArea(HomeActivity.this, "New in your area");
                AppPreferences.setFilterDatatype(getApplicationContext(), "new area");
                fragment = new HomeFragment();
                title = getString(R.string.title_activity_home_fragment);
                break;
            case 5:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Install this app :- ");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, AppConstants.APPSTOREURL);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
                break;
            case 6:
                Intent i = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(i);
                break;
            case 7:
                fragment = new HomeFragment();
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void showAppQuitAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (homeActivityInstance != null) {
                    homeActivityInstance.finish();
                }

                dialog.dismiss();
                Runtime.getRuntime().gc();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Runtime.getRuntime().gc();
            }
        });
        AlertDialog msg = builder.create();
        msg.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homeActivityInstance = null;
        AppPreferences.setFilterDatatype(this, "");
    }

    private void initiatePopupWindowSelectCategory() {
        try {
            LayoutInflater inflater = (LayoutInflater) HomeActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(
                    R.layout.activity_category_popup_main,
                    (ViewGroup) findViewById(R.id.pop_up_category_element));
            pwindo = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            lv = (ListView) layout.findViewById(R.id.listView1);
            alerText = (TextView) layout.findViewById(R.id.textViewalert);
            allCategoryCheckbox = (CheckBox) layout.findViewById(R.id.check1);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View item,
                                        int position, long id) {
                    AllCategoryModel allCategoryModel = allCategoryListAdapter
                            .getItem(position);
                    allCategoryModel.toggleChecked();
                    AllCategoryViewHolder viewHolder = (AllCategoryViewHolder) item
                            .getTag();
                    viewHolder.getCheckBox().setChecked(
                            allCategoryModel.isChecked());
                }
            });

            catogaryList = db.getCategoryList();

            ArrayList<String> categoryMyList = new ArrayList<String>();
            AllCategoryModel allCategoryModel;

            if (catogaryList != null && catogaryList.size() > 0) {

                allCategoryModelArray = (AllCategoryModel[]) getLastNonConfigurationInstance();
                System.out
                        .println(" allCategoryModelArray this -------------------- "
                                + allCategoryModelArray);
                if (allCategoryModelArray == null) {
                    allCategoryModelArray = new AllCategoryModel[catogaryList
                            .size()];
                    for (int i = 0; i < catogaryList.size(); i++) {
                        Category cat = catogaryList.get(i);
                        allCategoryModel = new AllCategoryModel(
                                cat.getCategory_name(),
                                cat.getServer_categoryid());

                        if (!AppPreferences.getFiltercategoryid(
                                getApplicationContext()).equalsIgnoreCase("")) {
                            String[] idArray = AppPreferences
                                    .getFiltercategoryid(
                                            getApplicationContext()).split(",");
                            System.out
                                    .println("filter criteria : "
                                            + AppPreferences
                                            .getFiltercategoryid(getApplicationContext()));
                            for (int k = 0; k < idArray.length; k++) {
                                if (Integer.parseInt(idArray[k]) == cat
                                        .getServer_categoryid()) {
                                    allCategoryModel.setChecked(true);
                                }
                            }

                        } else {
                            allCategoryCheckbox.setChecked(true);
                            allCategoryModel.setChecked(true);
                        }
                        allCategoryModelArray[i] = allCategoryModel;
                    }

                }
            } else {
                alerText.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                cancle_category = (Button) layout.findViewById(R.id.cross);
                cancle_category
                        .setOnClickListener(cancle_category_button_click_listener);
            }

            ArrayList<AllCategoryModel> allcategoryList = new ArrayList<AllCategoryModel>();
            allcategoryList.addAll(Arrays.asList(allCategoryModelArray));

            allCategoryListAdapter = new AllCategoryArrayAdapter(this,
                    allcategoryList);
            lv.setAdapter(allCategoryListAdapter);

            allCategoryCheckbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {
                                for (int i = 0; i < allCategoryModelArray.length; i++) {
                                    AllCategoryModel allCategoryModel = allCategoryModelArray[i];
                                    allCategoryModel.setChecked(true);
                                }

                                ArrayList<AllCategoryModel> allcategoryL = new ArrayList<AllCategoryModel>();
                                allcategoryL.addAll(Arrays
                                        .asList(allCategoryModelArray));
                                allCategoryListAdapter
                                        .updateResults(allcategoryL);
                            } else {
                                if (ckeckBoxChildStatus.equalsIgnoreCase("childClick")) {
                                    Log.d("2.1", ckeckBoxChildStatus);

                                    ckeckBoxChildStatus = "";
                                } else {
                                    Log.d("2.2", ckeckBoxChildStatus);
                                    for (int i = 0; i < allCategoryModelArray.length; i++) {
                                        AllCategoryModel allCategoryModel = allCategoryModelArray[i];
                                        allCategoryModel.setChecked(false);
                                    }
                                }

                                ArrayList<AllCategoryModel> allcategoryL = new ArrayList<AllCategoryModel>();
                                allcategoryL.addAll(Arrays
                                        .asList(allCategoryModelArray));
                                allCategoryListAdapter
                                        .updateResults(allcategoryL);
                            }
                        }
                    });

            cancle_category = (Button) layout.findViewById(R.id.cross);
            cancle_category
                    .setOnClickListener(cancle_category_button_click_listener);

            apply_category = (Button) layout
                    .findViewById(R.id.apply_category_button);
            apply_category.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (allCategoryCheckbox.isChecked()) {
                        AppPreferences.setFiltercategoryid(
                                getApplicationContext(), "");
                    } else {
                        AppPreferences.setFiltercategoryid(
                                getApplicationContext(), "");
                        for (int i = 0; i < allCategoryListAdapter.getCount(); i++) {
                            AllCategoryModel allCategoryModel = allCategoryListAdapter
                                    .getItem(i);
                            if (allCategoryModel.isChecked()) {
                                System.out.println(" name : "
                                        + allCategoryModel.getName() + " id : "
                                        + allCategoryModel.getId());

                                if (AppPreferences.getFiltercategoryid(
                                        getApplicationContext())
                                        .equalsIgnoreCase("")) {
                                    AppPreferences.setFiltercategoryid(
                                            getApplicationContext(), ""
                                                    + allCategoryModel.getId());
                                } else {
                                    String temp = AppPreferences
                                            .getFiltercategoryid(getApplicationContext());
                                    AppPreferences.setFiltercategoryid(
                                            getApplicationContext(), temp + ","
                                                    + allCategoryModel.getId());
                                }

                            }

                            System.out.println("all category filter criteri ***************"
                                    + AppPreferences
                                    .getFiltercategoryid(getApplicationContext()));
                        }
                    }

                    if (!allCategoryCheckbox.isChecked() && AppPreferences.getFiltercategoryid(getApplicationContext()).equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "At least one category should be selected.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        pwindo.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancle_category_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();
        }
    };

    private static class AllCategoryModel {
        private String name = "";
        private boolean checked = false;
        private int id;

        public AllCategoryModel() {
        }

        public AllCategoryModel(String name) {
            this.name = name;
        }

        public AllCategoryModel(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public AllCategoryModel(String name, boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String toString() {
            return name;
        }

        public void toggleChecked() {
            checked = !checked;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * Custom adapter for displaying an array of Category data objects.
     */
    private static class AllCategoryArrayAdapter extends
            ArrayAdapter<AllCategoryModel> {
        private LayoutInflater inflater;
        private List<AllCategoryModel> allCategoryModelList;

        public AllCategoryArrayAdapter(Context context,
                                       List<AllCategoryModel> allCategoryModelList) {
            super(context, R.layout.category_list_item, R.id.textView1,
                    allCategoryModelList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        public void updateResults(List<AllCategoryModel> d) {
            this.allCategoryModelList = d;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AllCategoryModel allCategoryModel = (AllCategoryModel) this
                    .getItem(position);

            CheckBox checkBox;
            TextView textView;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.category_list_item,
                        null);

                textView = (TextView) convertView.findViewById(R.id.textView1);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView
                        .setTag(new AllCategoryViewHolder(textView, checkBox));
                final CheckBox cb = checkBox;
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        AllCategoryModel allCategoryModel = (AllCategoryModel) cb
                                .getTag();
                        allCategoryModel.setChecked(cb.isChecked());

                        if (!cb.isChecked()) {
                            Log.d("sd1", ckeckBoxChildStatus);
                            ckeckBoxChildStatus = "childClick";
                            allCategoryCheckbox.setChecked(false);

                            Log.d("sd", ckeckBoxChildStatus);

                        }
                    }
                });
            }
            // Reuse existing row view
            else {
                AllCategoryViewHolder viewHolder = (AllCategoryViewHolder) convertView
                        .getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            checkBox.setTag(allCategoryModel);
            checkBox.setChecked(allCategoryModel.isChecked());
            textView.setText(allCategoryModel.getName());
            return convertView;
        }

    }

    private static class AllCategoryViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public AllCategoryViewHolder() {
        }

        public AllCategoryViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_fielter) {
            Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        finish();
    }


}