[原文链接](https://gist.github.com/polbins/e37206fbc444207c0e92) by [polbins](https://gist.github.com/polbins)

---
### Simple RecyclerView Divider
Simple Horizontal Divider Item Decoration for RecyclerView

```java
mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
            getApplicationContext()
        ));
```

**NOTE**: Add item decoration prior to setting the adapter

---
#### line_divider:

```java
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">


    <size
        android:width="1dp"
        android:height="1dp" />


    <solid android:color="@color/dark_gray" />


</shape>
```

---

#### SimpleDividerItemDecoration.java

```java
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;


    public SimpleDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();


        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);


            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();


            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();


            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
```
