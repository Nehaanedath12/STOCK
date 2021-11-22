package com.sangsolutions.stock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.StockAllocation.StockAllocationHistoryActivity;
import com.sangsolutions.stock.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    MainMenuAdapter menuAdapter;
    List<MainMenu> list;
    DatabaseHelper helper;


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();
        menuAdapter = new MainMenuAdapter(list);
        helper=new DatabaseHelper(this);

        list.add(new MainMenu(R.drawable.ic_sync, "Sync"));
        list.add(new MainMenu(R.drawable.ic_stock_count, "Stock Count"));
        
        try {
            if(list.size()>0){
                binding.menuGrid.setAdapter(menuAdapter);
                binding.menuGrid.setOnItemClickListener((parent, view12, position, id) -> {
                        switch (list.get(position).text) {

                            case "Stock Count":
                                startActivity(new Intent(getApplicationContext(), StockAllocationHistoryActivity.class));
                                break;
                            case "Sync":

                                Toast.makeText(this, "sync", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + list.get(position).text);
                        }
                });



            }

            binding.logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout?")
                            .setMessage("Do you want to Logout?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(helper.DeleteCurrentUser()){

                                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            });

        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,this);
        }
    }




    static class MainMenu{
        int Image;
        String text;

        public MainMenu(int image, String text) {
            Image = image;
            this.text = text;
        }

        public int getImage() {
            return Image;
        }

        public void setImage(int image) {
            Image = image;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    class MainMenuAdapter extends BaseAdapter {

        List<MainMenu> list;

        public MainMenuAdapter(List<MainMenu> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_item,parent,false);
            TextView text = view.findViewById(R.id.text);
            ImageView image = view.findViewById(R.id.image);

            text.setText(list.get(position).getText());
            image.setImageResource(list.get(position).getImage());
            return view;
        }
    }
}