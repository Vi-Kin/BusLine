package com.vi.busline.ToastUtil;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context,String content) {
        if (toast != null) {
            toast.cancel(); //取消
            toast = Toast.makeText(context,content,Toast.LENGTH_SHORT); //重新新建并显示吐司
        } else {
            toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}
