# Handling Runtime Changes

Some device configurations can change during runtime (such as screen orientation, keyboard availability, and language). When such a change occurs, Android restarts the running `Activity` (`onDestroy()` is called, followed by `onCreate()`). The restart behavior is designed to help your application adapt to new configurations by automatically reloading your application with alternative resources that match the new device configuration.

To properly handle a restart, it is important that your activity restores its previous state through the normal `Activity` lifecycle, in which Android calls `onSaveInstanceState()` before it destroys your activity so that you can save data about the application state. You can then restore the state during `onCreate()` or `onRestoreInstanceState()`.

To test that your application restarts itself with the application state intact, you should invoke configuration changes (such as changing the screen orientation) while performing various tasks in your application. Your application should be able to restart at any time without loss of user data or state in order to handle events such as configuration changes or when the user receives an incoming phone call and then returns to your application much later after your application process may have been destroyed. To learn how you can restore your activity state, read about the `Activity lifecycle`.

However, you might encounter a situation in which restarting your application and restoring significant amounts of data can be costly and create a poor user experience. In such a situation, you have two other options:

1. Retain an object during a configuration change

    Allow your activity to restart when a configuration changes, but carry a stateful object to the new instance of your activity.
2. Handle the configuration change yourself
    Prevent the system from restarting your activity during certain configuration changes, but receive a callback when the configurations do change, so that you can manually update your activity as necessary.


### Retaining an Object During a Configuration Change

If restarting your activity requires that you recover large sets of data, re-establish a network connection, or perform other intensive operations, then a full restart due to a configuration change might be a slow user experience. Also, it might not be possible for you to completely restore your activity state with the `Bundle` that the system saves for you with the `onSaveInstanceState()` callback—it is not designed to carry large objects (such as bitmaps) and the data within it must be serialized then deserialized, which can consume a lot of memory and make the configuration change slow. In such a situation, you can alleviate the burden of reinitializing your activity by retaining a `Fragment` when your activity is restarted due to a configuration change. This fragment can contain references to stateful objects that you want to retain.

When the Android system shuts down your activity due to a configuration change, the fragments of your activity that you have marked to retain are not destroyed. You can add such fragments to your activity to preserve stateful objects.

To retain stateful objects in a fragment during a runtime configuration change:

1. Extend the Fragment class and declare references to your stateful objects.
2. Call setRetainInstance(boolean) when the fragment is created.
3. Add the fragment to your activity.
4. Use FragmentManager to retrieve the fragment when the activity is restarted.

For example, define your fragment as follows:

```java
public class RetainedFragment extends Fragment {

    // data object we want to retain
    private MyDataObject data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(MyDataObject data) {
        this.data = data;
    }

    public MyDataObject getData() {
        return data;
    }
}
```
> **Caution**: While you can store any object, you should never pass an object that is tied to the `Activity`, such as a `Drawable`, an `Adapter`, a `View` or any other object that's associated with a `Context`. If you do, it will leak all the views and resources of the original activity instance. (Leaking resources means that your application maintains a hold on them and they cannot be garbage-collected, so lots of memory can be lost.)

Then use `FragmentManager` to add the fragment to the activity. You can obtain the data object from the fragment when the activity starts again during runtime configuration changes. For example, define your activity as follows:

```java
public class MyActivity extends Activity {

    private RetainedFragment dataFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag(“data”);

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment, “data”).commit();
            // load the data from the web
            dataFragment.setData(loadMyData());
        }

        // the data is available in dataFragment.getData()
        ...
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        dataFragment.setData(collectMyLoadedData());
    }
}
```

In this example, `onCreate()` adds a fragment or restores a reference to it. `onCreate()` also stores the stateful object inside the fragment instance. `onDestroy()` updates the stateful object inside the retained fragment instance.

### Handling the Configuration Change Yourself
If your application doesn't need to update resources during a specific configuration change and you have a performance limitation that requires you to avoid the activity restart, then you can declare that your activity handles the configuration change itself, which prevents the system from restarting your activity.

> **Note**: Handling the configuration change yourself can make it much more difficult to use alternative resources, because the system does not automatically apply them for you. This technique should be considered a last resort when you must avoid restarts due to a configuration change and is not recommended for most applications.

To declare that your activity handles a configuration change, edit the appropriate `<activity>` element in your manifest file to include the `android:configChanges` attribute with a value that represents the configuration you want to handle. Possible values are listed in the documentation for the `android:configChanges` attribute (the most commonly used values are `"orientation"` to prevent restarts when the screen orientation changes and `"keyboardHidden"` to prevent restarts when the keyboard availability changes). You can declare multiple configuration values in the attribute by separating them with a pipe `|` character.

For example, the following manifest code declares an activity that handles both the screen orientation change and keyboard availability change:

```
<activity android:name=".MyActivity"
          android:configChanges="orientation|keyboardHidden"
          android:label="@string/app_name">
```

Now, when one of these configurations change, `MyActivity` does not restart. Instead, the MyActivity receives a call to `onConfigurationChanged()`. This method is passed a `Configuration` object that specifies the new device configuration. By reading fields in the Configuration, you can determine the new configuration and make appropriate changes by updating the resources used in your interface. At the time this method is called, your activity's `Resources` object is updated to return resources based on the new configuration, so you can easily reset elements of your UI without the system restarting your activity.

> **Caution**: Beginning with Android 3.2 (API level 13), **the "screen size" also changes** when the device switches between portrait and landscape orientation. Thus, if you want to prevent runtime restarts due to orientation change when developing for API level 13 or higher (as declared by the `minSdkVersion` and `targetSdkVersion` attributes), you must include the `"screenSize"` value in addition to the `"orientation"` value. That is, you must decalare `android:configChanges="orientation|screenSize"`. However, if your application targets API level 12 or lower, then your activity always handles this configuration change itself (this configuration change does not restart your activity, even when running on an Android 3.2 or higher device).

For example, the following `onConfigurationChanged()` implementation checks the current device orientation:

```java
@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    // Checks the orientation of the screen
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
    }
}
```

The `Configuration` object represents all of the current configurations, not just the ones that have changed. Most of the time, you won't care exactly how the configuration has changed and can simply re-assign all your resources that provide alternatives to the configuration that you're handling. For example, because the `Resources` object is now updated, you can reset any `ImageViews` with `setImageResource()` and the appropriate resource for the new configuration is used (as described in `Providing Resources`).

Notice that the values from the `Configuration` fields are integers that are matched to specific constants from the `Configuration` class. For documentation about which constants to use with each field, refer to the appropriate field in the `Configuration` reference.

> **Remember**: When you declare your activity to handle a configuration change, you are responsible for resetting any elements for which you provide alternatives. If you declare your activity to handle the orientation change and have images that should change between landscape and portrait, you must re-assign each resource to each element during `onConfigurationChanged()`.

If you don't need to update your application based on these configuration changes, you can instead not implement `onConfigurationChanged()`. In which case, all of the resources used before the configuration change are still used and you've only avoided the restart of your activity. However, your application should always be able to shutdown and restart with its previous state intact, so you should not consider this technique an escape from retaining your state during normal activity lifecycle. Not only because there are other configuration changes that you cannot prevent from restarting your application, but also because you should handle events such as when the user leaves your application and it gets destroyed before the user returns to it.

For more about which configuration changes you can handle in your activity, see the [`android:configChanges`](http://developer.android.com/guide/topics/manifest/activity-element.html#config) documentation and the [`Configuration`](http://developer.android.com/reference/android/content/res/Configuration.html) class. [注1]

[注1]: 需要科学上网
