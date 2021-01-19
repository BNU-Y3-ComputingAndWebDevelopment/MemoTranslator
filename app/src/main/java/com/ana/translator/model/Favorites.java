package com.ana.translator.model;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ana.translator.Database.Database;

@Entity(tableName = "favorites")
public class Favorites {

    @PrimaryKey(autoGenerate = true)
    private int index;

    @ColumnInfo(name = "source")
    private String source;

    @ColumnInfo(name = "target")
    private String target;

    public Favorites(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @BindingAdapter("onShareClick")
    public static void shareOnClick(ImageView imageView, Favorites favorites)
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Source Text: "+favorites.getSource()+" Target Text: "+favorites.getTarget());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                imageView.getContext().startActivity(shareIntent);
            }
        });

    }

    @BindingAdapter("onDeleteClick")
    public static void deleteOnClick(ImageView imageView, Favorites favorites)
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());
                builder.setMessage("Are you Sure to Delete ?")
                        .setCancelable(false)
                        .setTitle("Are you Sure")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Database database = Database.getLocalDatabase(imageView.getContext());
                                database.userDao().deletefavorites(favorites);
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(imageView.getContext().getResources().getColor(android.R.color.holo_green_light));
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(imageView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    }
                });
                alert.show();

            }
        });

    }
}
