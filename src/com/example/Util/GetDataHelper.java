package com.example.Util;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by smallQ on 15-4-3.
 */

//这个类用来从缓存优先获取数据
public class GetDataHelper
{


	BlockingQueue<Entity> mListQueue = new ArrayBlockingQueue<>(N);
	static int mPageNumber = 0;
	static int mPosition = 0;
	static int mListNumber = 0;
	public static final int N = 20;
	Context mContext;

	public GetDataHelper(Context context)
	{
		this.mContext = context;
	}

	//保存N个list供下次启动软件时加载
	public void saveNlist(ArrayList<Entity> list)
	{
		ArrayList<Entity> subList = new ArrayList<>();
		int size = list.size();
		if (size < N)
		{
			subList.addAll(list.subList(0, size));
		}
		else
		{
			subList.addAll(list.subList(0, N));
		}
		//Log.e("保存的list-----",subList+"");
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try
		{
			//Log.e("mContext.deleteFile-----",mContext.deleteFile("five_list")+"");
			mContext.deleteFile("five_list");
			os = mContext.openFileOutput("five_list", Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(os);
			oos.writeObject(subList);
			oos.close();
			
		}
		catch (IOException e)
		{
			// Log.e(this, "file write failure");
			e.printStackTrace();
		}
	}

	//从存储器恢复N个list
	public ArrayList<Entity> restoreNlist()
	{ 
		ArrayList<Entity> getedList = new ArrayList<Entity>();
		try
		{   InputStream is = null;
		is = mContext.openFileInput("five_list");
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(is);
		Object object = ois.readObject();
		ois.close();
		getedList = ((ArrayList<Entity>) object);
		
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		//Log.e("从文件恢复的list----",getedList+"");
		return getedList;

	}

	//清空mListPool
	public void clearListPool()
	{
		mListQueue.clear();
		mPageNumber = 0;
		mPosition = 0;
		mListNumber = 0;
	}

	/*  //从网络获得最新的N条
    public ArrayList<Entity> getNewN()
    {
    	ArrayList<Entity> list = new ArrayList<Entity>();
        for (int i = 0; i < N; i++)
        {
            try
            {
               // list.add(mHomePageWeb.getWhichListOfWhichPage((0 + i) / 20 + 1, (0 + i) % 20));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }

    //从网络获得N条list添加到mListQueue中
    public synchronized void freshListFromNet(int position)
    {
        for (int i = 0; i < N; i++)
        {
            try
            {
               // mListQueue.add(mHomePageWeb.getWhichListOfWhichPage((position + i) / 20 + 1, (position + i) % 20));
                //Log.i(this, "第" + ((position + i) / 20 + 1) + "页," + "第" + (position + i) % 20 + "项");
            }
            catch (IllegalStateException e)
            {
               // Log.i(this, "队列满" + mListQueue.size());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
               // Log.e(this, "没有更多的list了");
                e.printStackTrace();
            }
        }
       // Log.v(this, "传入的position为：" + position);
    }*/

	//从mListPool获取N条list
	public ArrayList<Entity> getList()
	{
		ArrayList<Entity> list = new ArrayList<>();
		for (int i = 0; i < N; i++)
		{
			try
			{
				list.add(mListQueue.take());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

}
