package com.cdac.kp.androidchat;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdac.kp.androidchat.interfaces.Manager;
import com.cdac.kp.androidchat.type.InfoOfFriends;
import com.cdac.kp.androidchat.type.InfoStatus;

import org.dmc.cdac.myapplication.R;

public class ListOfFriends extends ListActivity
{
    public static final int ADD_NEW_FRIEND = Menu.FIRST;
    public static final int EXIT  = Menu.FIRST + 1;

    public Manager serviceProvider;

    public FriendListAdapter friendListAdapter;

    public class FriendListAdapter extends BaseAdapter
    {
        class ViewHolder
        {
            TextView text;
            ImageView icon;
        }
        public LayoutInflater inflater;
        public Bitmap onlineIcon;
        public Bitmap offlineIcon;

        public InfoOfFriends[] friends = null;


        public FriendListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
            onlineIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.online);
            onlineIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.offline);
        }

        public void setFriends(InfoOfFriends[] friends) {
            this.friends = friends;
        }

        @Override
        public int getCount() {
            return friends.length;
        }

        @Override
        public InfoOfFriends getItem(int position) {
            return friends[position];
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.list_friend_screen, null);

                holder = new ViewHolder();
                holder.text = (TextView)convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.text.setText(friends[position].userName);
            holder.icon.setImageBitmap(friends[position].status == InfoStatus.ONLINE ? onlineIcon : offlineIcon);

            return convertView;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_friends);
    }
}
