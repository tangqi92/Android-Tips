[原文链接](http://baoyz.com/android/2014/10/21/android-palette-use/) (*需要科学上网*)

## Android Lollipop 新特性 - Palette

> *Palette 可以从一张图片中提取颜色，我们可以把提取的颜色融入到App UI中，可以使UI风格更加美观融洽。比如，我们可以从图片中提取颜色设置给ActionBar做背景颜色，这样ActionBar的颜色就会随着显示图片的变化而变化。*

Palette可以提取的颜色如下:
+ Vibrant （有活力的）
+ Vibrant dark（有活力的 暗色）
+ Vibrant light（有活力的 亮色）
+ Muted （柔和的）
+ Muted dark（柔和的 暗色）
+ Muted light（柔和的 亮色）

### 使用方法

> *我们要想使用Palette，需要导入Palette的兼容库，`Gradle` 中添加下面依赖。*

```
compile 'com.android.support:palette-v7:21.0.0'
```

第一步，我们需要通过一个Bitmap对象来生成一个对应的Palette对象。
Palette 提供了四个静态方法用来生成对象。

+ `Palette generate(Bitmap bitmap)`
+ `Palette generate(Bitmap bitmap, int numColors)`
+ `generateAsync(Bitmap bitmap, PaletteAsyncListener listener)`
+ `generateAsync(Bitmap bitmap, int numColors, final PaletteAsyncListener listener)`

> *不难看出，生成方法分为`generate`(同步)和`generateAsync`(异步)两种，如果图片过大使用generate方法，可能会阻塞主线程，我们更倾向于使用`generateAsync`的方法，其实内部就是创建了一个`AsyncTask`。`generateAsync`方法需要一个`PaletteAsyncListener`对象用于监听生成完毕的回调。除了必须的`Bitmap`参数外，还可以传入一个`numColors`参数指定颜色数，默认是 16。*

第二步，得到Palette对象后，就可以拿到提取到的颜色值

+ `Palette.getVibrantSwatch()`
+ `Palette.getDarkVibrantSwatch()`
+ `Palette.getLightVibrantSwatch()`
+ `Palette.getMutedSwatch()`
+ `Palette.getDarkMutedSwatch()`
+ `Palette.getLightMutedSwatch()`

第三步，使用颜色，上面get方法中返回的是一个 `Swatch` 样本对象，这个样本对象是Palette的一个内部类，它提供了一些获取最终颜色的方法。

+ `getPopulation()`: 样本中的像素数量
+ `getRgb()`: 颜色的RBG值
+ `getHsl()`: 颜色的HSL值
+ `getBodyTextColor()`: 主体文字的颜色值
+ `getTitleTextColor()`: 标题文字的颜色值

> *通过 `getRgb()` 可以得到最终的颜色值并应用到UI中。`getBodyTextColor()` 和 `getTitleTextColor()` 可以得到此颜色下文字适合的颜色，这样很方便我们设置文字的颜色，使文字看起来更加舒服。*

### 实例代码

```java
// 此方法可能会阻塞主线程，建议使用异步方法
Palette palette = Palette.generate(bitmap);   
// 异步提取Bitmap颜色
Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
    @Override
    public void onGenerated(Palette palette) {
        // 提取完毕
        // 有活力的颜色
        Palette.Swatch vibrant = palette.getVibrantSwatch();    
        // 有活力的暗色
        Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();    
        // 有活力的亮色
        Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();    
        // 柔和的颜色
        Palette.Swatch muted = palette.getMutedSwatch();    
        // 柔和的暗色
        Palette.Swatch darkMuted = palette.getDarkMutedSwatch();    
        // 柔和的亮色
        Palette.Swatch lightMuted = palette.getLightMutedSwatch();    
 
        // 使用颜色
        // 修改Actionbar背景颜色
        getActionBar().setBackgroundDrawable(new ColorDrawable(vibrant.getRgb()));
        // 修改文字的颜色
        mTextView.setTextColor(vibrant.getTitleTextColor());
        ...
        // 根据需求选择不同效果的颜色应用
    });
```

### 效果

![pic](https://raw.githubusercontent.com/baoboy/baoboy.github.io/master/images/screenshots/palette_3.png)

### 代码下载

[https://github.com/baoyongzhang/android-lollipop-samples](https://github.com/baoyongzhang/android-lollipop-samples)中的[palette-sample](https://github.com/baoyongzhang/android-lollipop-samples/tree/master/palette-sample)
