[原文链接](http://droidyue.com/blog/2014/07/16/better-in-android-include-attrs-in-declare-stylable/)(*需要科学上网*)

## 自定义控件进阶:declare-styleable重用attr

最近接触了Android自定义控件,涉及到自定义xml中得属性(attribute),其实也很简单,但是写着写着,发现代码不完美了,就是在attrs.xml这个文件中,发现属性冗余,于是就想有没有类似属性继承或者include之类的方法.本文将就declare-stylable中属性重用记录一下.

---

### 不完美的代码

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <declare-styleable name="ExTextView">
       <attr name="enableOnPad" format="boolean" />
     <attr name="supportDeviceType" format="reference"/>
    </declare-styleable>

    <declare-styleable name="ExEditText">
       <attr name="enableOnPad" format="boolean" />
     <attr name="supportDeviceType" format="reference"/>  
    </declare-styleable>
</resources>
```

如上面代码,在ExTextView和ExEditText这个stylable中有着重复的属性申明.虽然上面可以工作,但是总感觉写的不专业,于是寻找优化方法.

---

### 还可以这样么

尝试着为declare-stylable指定一个parent,如下代码

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <declare-styleable name="ExTextView">
       <attr name="enableOnPad" format="boolean" />
     <attr name="supportDeviceType" format="reference"/>
    </declare-styleable>

    <declare-styleable name="ExEditText" parent="ExTextView">

    </declare-styleable>
</resources>
```

attrs.xml没有报告语法错误.但是当我使用R.styleable.ExEditText_supportDeviceType时候,R文件却没有生成,重新清理了工程还是不生效,不知道是否为adt插件的问题.其他人也遇到了这样的问题. 这个方法目前是不行的.

---

### 终极答案

实际上我们可以在declare-stylable之前,申明要多次使用的属性,在declare-stylable节点内部,只需调用即可.具体代码如下.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <attr name="enableOnPad" format="boolean" />
  <attr name="supportDeviceType" format="reference"/>

    <declare-styleable name="ExTextView">
        <attr name="enableOnPad"/>
        <attr name="supportDeviceType"/>
    </declare-styleable>

    <declare-styleable name="ExEditText">
        <attr name="enableOnPad"/>
      <attr name="supportDeviceType"/>
    </declare-styleable>
</resources>
```

每次引用attr后,建议清理一下工程,确保R文件重新生成.

#### 延伸阅读

+ [http://stackoverflow.com/questions/18827875/how-to-declare-several-stylable-attributes-with-the-same-name-for-different-tags](http://stackoverflow.com/questions/18827875/how-to-declare-several-stylable-attributes-with-the-same-name-for-different-tags)

#### 其他

+ [Android系统源代码情景分析](http://www.amazon.cn/gp/product/B009OLU8EE/ref=as_li_tf_tl?ie=UTF8&camp=536&creative=3200&creativeASIN=B009OLU8EE&linkCode=as2&tag=droidyue-23)
