
# Android Jetpack架构相关组件和库的使用简介

> **Jetpack** 是一个由多个库组成的套件，可帮助开发者遵循最佳做法，减少样板代码并编写可在各种 Android 版本和设备中一致运行的代码，让开发者精力集中编写重要的代码。

## Jetpack的优点

> - 加速开发：组件可以单独采用（不过这些组件是为协同工作而构建的），同时利用 Kotlin 语言功能帮助您提高工作效率。
> 
> - 消除样板代码：Android Jetpack 可管理繁琐的 Activity（如后台任务、导航和生命周期管理），以便您可以专注于如何让自己的应用出类拔萃。
> 
> - 构建高质量的强大应用：Android Jetpack 组件围绕现代化设计实践构建而成，具有向后兼容性，可以减少崩溃和内存泄漏。

### 架构原则：

> **1. 分离关注点**：基于界面的类应仅包含处理界面和操作系统交互的逻辑
>
> **2.通过模型驱动界面**：模型是负责处理应用数据的组件。它们独立于应用中的 View 对象和应用组件，因此不受应用的生命周期以及相关的关注点的影响
>
> 持久性是理想之选，原因如下：
> 
> - 如果 Android 操作系统销毁应用以释放资源，用户不会丢失数据。
> 
> - 当网络连接不稳定或不可用时，应用会继续工作。
> 
> - 应用所基于的模型类应明确定义数据管理职责，这样将使应用更可测试且更一致



### 推荐应用架构



![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/4f1c7ef0ea9a77c4eb9b01c4ce606627.png#pic_center)




## Jetpack架构组件：

.

.


# [1.ViewModel](https://developer.android.google.cn/topic/libraries/architecture/viewmodel)

#### 介绍：

> 对象为特定的界面组件（如 Fragment 或Activity）提供数据，并包含数据处理业务逻辑，以与模型进行通信。例如，ViewModel 可以调用其他组件来加载数据，还可以转发用户请求来修改数据。ViewModel 不了解界面组件，因此不受配置更改（如在旋转设备时重新创建Activity）的影响。

#### 作用：

> 负责为Fragment/Activity配置和管理数据的类，同时处理Fragment/Activity与Application其余部分的通信


#### 解决的问题：

> - **ViewModel 的存在，主要是为了解决 状态管理 和 页面通信 的问题**
> 
> - 当Actvitiy销毁重建过程中的数据恢复问题，虽然原来可以使用onSaveInstanceState()来完成，但是只支持能被序列化的数据而且是小量数据，对于大量数据则显得有点无力。
> 
> - UI控制器的工作繁忙，UI控制器主要用于处理显示，交互，其他的额外操作可以委托给其他类完成，将不应该分配给UI的任务分离出来是必要的，这也就是上面所说的分离关注点原则



### **ViewModel 的生命周期**


> ViewModel 对象存在的时间范围是获取 ViewModel 时传递给 ViewModelProvider 的 Lifecycle。ViewModel 将一直留在内存中，直到限定其存在时间范围的 Lifecycle 永久消失：对于Activity，是在 Activity 完成时；而对于 Fragment，是在 Fragment 分离时。

###### 下图说明：

>  Activity 经历屏幕旋转而后结束的过程中所处的各种生命周期状态。该图还在关联的 Activity 生命周期的旁边显示了ViewModel 的生命周期。此图表说明了 Activity 的各种状态。这些基本状态同样适用于 Fragment 的生命周期。

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/9a65d42f8102c0aa36d8894d392b0740.png#pic_center)



## ViewModel的使用

### 在Activity引入 


> **使用步骤，定义了以下文件：**
> 
>  1. **user_profile.xml：屏幕的界面布局定义。**
>     
>  2. **MyViewModel.java：准备数据以便在 UserProfileFragment 中查看并对用户互动做出响应的类**
>     
> 3. **MainActivity.java：显示数据的界面控制器。**


**MyViewModel.java 文件：**

		
```java
/*
* 只要使用androidx，就等于导入JerPack库
* 作用：把MainActivity的数据Model给抽取出来
* 层级关系：Model里装载着LiveData 和 业务逻辑
* 注意：
    1.如果不需要Context上下文环境的可以继承ViewModel类，如果需要Context上下文环境就需要继承AndroidViewModel类
* */

//不需要Context上下文环境
public class MyViewModel extends ViewModel {
 	public int num = 0;

}

//需要Context上下文环境
public class MyViewModel extends AndroidViewModel {

	 private Context context;

    //继承AndroidViewModel类，必须要实现的构造方法
    public MainViewModel(@NonNull Application application) {
        super(application);

        this.context=application.getApplicationContext();//获取上下文环境
    }
	
	 public int num = 0;

}
```




**MainActivity.java 文件**

```java
public class MainActivity extends AppCompatActivity {

private MyViewModel myViewModel;//定义ViewModel对象
private TextView tv;
private Button button1, button2;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

	//this不加依赖会报错（下面有说明）
    myViewModel = new ViewModelProvider(this).get(MyViewModel.class);//初始化ViewMode

    tv = findViewById(R.id.textView);
    tv.setText(String.valueOf(myViewModel.num));
    button1 = findViewById(R.id.button1);
    button2 = findViewById(R.id.button2);

    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myViewModel.num++;
            tv.setText(String.valueOf(myViewModel.num));
        }
    });

    button2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myViewModel.num += 2;
            tv.setText(String.valueOf(myViewModel.num));
        }
    });
	}
}
```

> **注意：**
>
>**ViewModelProvider this报错的问题**
```java
myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
```
>**this不加依赖会报错：**
>build.gradle里加两个依赖：
```java
   implementation 'com.google.android.material:material:1.0.0'
   implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
```


### 在Fragment引入 
创建ViewModel类

```java
public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Item> selected = new MutableLiveData<Item>();
    
    public void select(Item item) {
    	selected.setValue(item);
    }
    
    public LiveData<Item> getSelected() {
    	return selected;
    }
}
```
  
  **在Fragment中使用ViewModel**
    
```java
public class MasterFragment extends Fragment {
    
	private SharedViewModel model;

    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

	    itemSelector.setOnClickListener(item -> {

	    	model.select(item);

	    	});
	    }
    }
```
	    
**或者**

```java
public class DetailFragment extends Fragment {

  public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

	    model.getSelected().observe(this, { item ->

	       //更新UI

	    });
    }
}
```


**注意：**

>   这两个 Fragment 都会检索包含它们的 Activity。这样，当这两个 Fragment 各自获取 `ViewModelProvider` 时，它们会收到相同的 SharedViewModel 实例（其范围限定为该 Activity）。

**此方法具有以下优势：**

> - **Activity 不需要执行任何操作，也不需要对此通信有任何了解。**
> 
> - **除了 SharedViewModel 约定之外，Fragment 不需要相互了解。如果其中一个 Fragment 消失，另一个 Fragment 将继续照常工作。**
> 
> - **每个 Fragment 都有自己的生命周期，而不受另一个 Fragment 的生命周期的影响。如果一个 Fragment 替换另一个 Fragment，界面将继续工作而没有任何问题。**


## 加强ViewModel，支持异常生命周期

> - **有些时候在Activity被意外杀死回调onSaveInstanceState()异常杀死下的生命周期，这个时候ViewModel也会被杀死，再次恢复的时候便会被重建，这样，原来的数据也就丢失了，因此我们需要改进一下ViewModel以支持异常退出情况下的重建。**
> 
> - **Activity通过onSaveInstanceState() 来保存，然后通过SaveInstanceState 来恢复，ViewModel也提供了类似SavedInstanceState的方法。** 
> 
> **就是：**
>   SavedStateHandle ：用于保存状态的数据类型，是一个key-value的map，其实也就是类似Bundle。采用Hashmap来实现的。


### 具体使用：


**ViewModelWithData.java文件：**

```java
public class ViewModelWithData extends ViewModel {

   
	private SavedStateHandle handle;

	private static final String KEY_NUMBER = "number";

    public ViewModelWithData(SavedStateHandle handle) {
        this.handle = handle;

    }
	
	//每次都通过SavedStateHandle来获取相应的值
    public MutableLiveData<Integer> getNumber() {

        if (!handle.contains(KEY_NUMBER)) {
            handle.set(KEY_NUMBER, 0);
        }

        return handle.getLiveData(KEY_NUMBER);
    }

    public void addNumber(int n) {

        getNumber().setValue(getNumber().getValue() + n);

    }
}
```

**LiveDataActivity.java**

```java
public class LiveDataActivity extends AppCompatActivity {

	private ViewModelWithData viewModelWithData;

    ActivityLiveDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_data);

		//需要将创建的SavedStateVMFactory传入
        viewModelWithData =  new ViewModelProvider(this).get(ViewModelWithData.class);//初始化ViewMode
		
		//建立观察者（布局）和ViewModel的感应
        binding.setData(viewModelWithData);
		
        binding.setLifecycleOwner(this);

    }

}
```
> **注：**
>  上面的 ActivityLiveDataBinding 对象是根据 `R.layout.activity_live_data` 的布局文件来系统帮我们自动创建的对象



## 将加载器替换为 ViewModel

> CursorLoader 等加载器类经常用于使应用界面中的数据与数据库保持同步。您可以将 ViewModel
> 与一些其他类一起使用来替换加载器。使用 ViewModel 可将界面控制器与数据加载操作分离，这意味着类之间的强引用更少。
> 
> 在使用加载器的一种常见方法中，应用可能会使用 CursorLoader
> 观察数据库的内容。当数据库中的值发生更改时，加载器会自动触发数据的重新加载并更新界面




![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/005bddd971b596ef6fb5f7c75cc9e46f.png#pic_center)
使用加载器加载数据



> ViewModel 与 Room 和 LiveData 一起使用可替换加载器。ViewModel 确保数据在设备配置更改后仍然存在。Room
> 在数据库发生更改时通知 LiveData，LiveData 进而使用修订后的数据更新界面。

![](https://img-blog.csdnimg.cn/img_convert/2d5a6c81b107b169b5d4f1f981529267.png#pic_center)

使用 ViewModel 加载数据


.

.



# [2. LiveDate](https://developer.android.google.cn/topic/libraries/architecture/livedata#extend_livedata)

#### 介绍：

> **LiveData**是一种可观察的数据存储器类。
> - 与常规的可观察类不同，LiveData 具有生命周期感知能力，意指它遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期。这种感知能力可确保 LiveData仅更新处于活跃生命周期状态的应用组件观察者。
>
>- 如果观察者（由 Observer 类表示）的生命周期处于 STARTED 或 RESUMED 状态，则 LiveData会认为该观察者处于活跃状态。LiveData 只会将更新通知给活跃的观察者。为观察 LiveData对象而注册的非活跃观察者不会收到更改通知。


###  LiveData 的优势：

>  **1. 确保界面符合数据状态**
> - LiveData 遵循观察者模式。当生命周期状态发生变化时，LiveData 会通知 Observer(观察者)对象。您可以整合代码以在这些 Observer 对象中更新界面。观察者可以在每次发生更改时更新界面，而不是在每次应用数据发生更改时更新界面。
>
>  **2. 不会发生内存泄漏**
> - 观察者会绑定到 Lifecycle 对象，并在其关联的生命周期遭到销毁后进行自我清理。
>
>  **3. 不会因 Activity 停止而导致崩溃**
> - 如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），则它不会接收任何 LiveData 事件。
> 
> **4. 不再需要手动处理生命周期**
>- 界面组件只是观察相关数据，不会停止或恢复观察。LiveData 将自动管理所有这些操作，因为它在观察时可以感知相关的生命周期状态变化。
>
> **5. 数据始终保持最新状态**
>- 如果生命周期变为非活跃状态，它会在再次变为活跃状态时接收最新的数据。例如，曾经在后台的 Activity 会在返回前台后立即接收最新的数据。
>
>  **6. 适当的配置更改**
>- 如果由于配置更改（如设备旋转）而重新创建了 Activity 或 Fragment，它会立即接收最新的可用数据。
>
>  **7. 共享资源**
>- 您可以使用单一实例模式扩展 LiveData 对象以封装系统服务，以便在应用中共享它们。LiveData对象连接到系统服务一次，然后需要相应资源的任何观察者只需观察 LiveData 对象

#### 解决的问题：

> LiveData 的存在，主要是为了帮助 新手老手 都能不假思索地遵循 通过唯一可信源分发状态 的标准化开发理念，从而使在快速开发过程中
> 难以追溯、难以排查、不可预期 的问题所发生的概率降低到最小



## LiveData使用：

**LiveData使用步骤如下所示：**

##### 1.创建 LiveData

创建 LiveData 实例以存储某种类型的数据。这通常在 ViewModel 类中完成。

```java
public class NameViewModel extends ViewModel {

// 创建一个字符串类型的LiveData对象
private MutableLiveData<String> currentName;
	
    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        return currentName;
    }

// ViewModel的其余部分...

}
```

   

> **注意：**
> 请确保用于更新界面的 LiveData 对象存储在 ViewModel 对象中，而不是将其存储在 Activity 或 Fragment 中，原因如下：
> 
> - 避免 Activity 和 Fragment 过于庞大。现在，这些界面控制器负责显示数据，但不负责存储数据状态。
> 
> - 将 LiveData 实例与特定的 Activity 或 Fragment 实例分离开，并使 LiveData 对象在配置更改后继续存在。

 

##### 2.观察 LiveData 对象

创建可定义 onChanged() 方法的 Observer 对象，该方法可以控制当 LiveData 对象存储的数据更改时会发生什么。通常情况下，您可以在界面控制器（如 Activity 或 Fragment）中创建 Observer 对象。

```java
public class NameActivity extends AppCompatActivity {

    private NameViewModel model;
	private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 用于设置活动的其他代码...

        // 得到ViewModel（初始化）
        model = new ViewModelProvider(this).get(NameViewModel.class);

        //创建用于更新UI的观察者
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {

                // 更新UI，这里是TextView
                nameTextView.setText(newName);
            }
        };

        // 观察LiveData，将此活动作为LifecycleOwner和observer传递进来
        model.getCurrentName().observe(this, nameObserver);
    }
}
```
    

#####  3.更新 LiveData 对象

LiveData 没有公开可用的方法来更新存储的数据。MutableLiveData 类将公开 setValue(T) 和 postValue(T) 方法，如果您需要修改存储在 LiveData 对象中的值，则必须使用这些方法。通常情况下会在 ViewModel 中使用 MutableLiveData，然后 ViewModel 只会向观察者公开不可变的 LiveData 对象。

```java
button.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        String anotherName = "John Doe";
        model.getCurrentName().setValue(anotherName);
    }
});
```
    

> **注意：**
> 您必须调用 setValue(T) 方法以从主线程更新 LiveData 对象。如果在 worker 线程中执行代码，则您可以改用 postValue(T) 方法来更新 LiveData 对象。

#### 将 LiveData 与 Room 一起使用

> Room 持久性库支持返回 LiveData 对象的可观察查询。可观察查询属于数据库访问对象 (DAO) 的一部分。
>- 当数据库更新时，Room 会生成更新 LiveData对象所需的所有代码。在需要时，生成的代码会在后台线程上异步运行查询。此模式有助于使界面中显示的数据与存储在数据库中的数据保持同步。您可以在Room 持久性库指南中详细了解 Room 和 DAO。

.

.

# [3.DataBinding](https://developer.android.google.cn/topic/libraries/data-binding/start)

### 介绍：

DataBinding的存在是为了脱离Controller与View之间的引用联系如下图所示：

![](https://img-blog.csdnimg.cn/20190906134311770.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MTI0,size_16,color_FFFFFF,t_70#pic_center)

这样，Controller只处理与业务逻辑相关的操作。

### 解决的问题：

>  **DataBinding 的存在，主要是为了解决 视图调用 的一致性问题**



### DataBinding使用：

**1. 首先在build.gradle中开启DadaBinding**

```java
android {
    ...
    dataBinding {
        enabled = true
    }
}
```
    
**2. 在XML中使用DadaBinding**
> 数据绑定布局文件略有不同，必须以 layout 标记开头，后跟 data 元素和 view 根元素。 view 元素必须是一个非绑定的布局文件。下面是布局文件样例：

```java
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

	<!--这里表示要绑定的ViewModel对象-->
	<!--data 元素中的变量user描述了可在此布局中使用的属性-->
    <data>
        <variable
            name="data"
            type="com.example.ViewModelWithData" />
    </data>

    <androidx.appcompat.widget.LinearLayoutCompat>
		
		<!--布局中的使用 “@{}” 语法来使用属性-->
		<!--绑定的ViewModel对象里的数据-->
        <TextView
            android:text="@{data.number.toString()}" />
		
		<!--绑定的ViewModel对象里的方法-->
        <Button
            android:onClick="@{()->data.addNumber(1)}" />

    </androidx.appcompat.widget.LinearLayoutCompat>
</layout>
```


**3. 在Activity中的引用逻辑**

```java
public class LiveDataActivity extends AppCompatActivity {

	private ViewModelWithData viewModelWithData;

	private ActivityLiveDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		//给DataBinding绑定视图
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_data);
		
		//初始化ViewModel
        viewModelWithData = new ViewModelProvider(this).get(ViewModelWithData.class);
		
		//设置DataBinding的数据来源
        binding.setData(viewModelWithData);

		//这个是这只数据的观察者，同时应用观察者的生命周期
        binding.setLifecycleOwner(this);	
    }
}
```


### DataBinding可以绑定的数据类型

**1. Data object(数据对象)**

```java
public class User {
  private final String firstName;
  private final String lastName;
  public User(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
  }
  public String getFirstName() {
      return this.firstName;
  }
  public String getLastName() {
      return this.lastName;
  }
}

//在Activity中引用
Override
protected void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
   User user = new User("Test", "User");
   binding.setUser(user);
}
```

支持的表达语言：

> - 基本运算 + / * %
> - 字符串连接 +
> - 逻辑表达式 && ||
> - 二进制 & | ^
> - 一元运算符 + ! ~
> - 移位 >> >>> <<
> - 比较运算 == > < >= <=
> - instanceof
> - 分组 ()
> - 字面值 字符，字符串，数字， null
> - 强转
> - 方法调用
> - 属性访问
> - 数组访问 []
> - 三元运算符 ?：

**举例：**

```java
android:text="@{String.valueOf(index + 1)}"
android:visibility="@{age < 13 ? View.GONE : View.VISIBLE}"
android:transitionName='@{"image_" + id}'

//空结合运算符
android:text="@{user.displayName ?? user.lastName}"
//属性引用
android:text="@{user.lastName}"
```

**2. 集合**

可以使用[]运算符访问普通集合，例如arrays、lists、sparse lists和maps

```java
<data>
    <import type="android.util.SparseArray"/>
    <import type="java.util.Map"/>
    <import type="java.util.List"/>
    <variable name="list" type="List&lt;String&gt;"/>
    <variable name="sparse" type="SparseArray&lt;String&gt;"/>
    <variable name="map" type="Map&lt;String, String&gt;"/>
    <variable name="index" type="int"/>
    <variable name="key" type="String"/>
</data>
…
android:text="@{list[index]}"
…
android:text="@{sparse[index]}"
…
android:text="@{map[key]}"
```

3.引用资源文件

```java
android:text="@{@string/nameFormat(firstName, lastName)}"
android:text="@{@plurals/banana(bananaCount)}"
```

**4. 方法引用**

 **方法类：**

```java
public class MyHandlers {
    public void onClickFriend(View view) { ... }
}
```

绑定表达式可以将View的单击监听器分配给 onClickFriend()方法，如下所示：

```java
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
   <data>
       <variable name="handlers" type="com.example.MyHandlers"/>
       <variable name="user" type="com.example.User"/>
   </data>
   <LinearLayout
       android:orientation="vertical"
       android:layout_width="match_parent"
       android:layout_height="match_parent">
       <TextView android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:text="@{user.firstName}"
           android:onClick="@{handlers::onClickFriend}"/>
   </LinearLayout>
</layout>
```

**5. Imports、variables 和 includes**

>数据绑定库提供导入，变量和包含等功能。导入使布局文件中的类很容易引用。变量允许您描述可用于绑定表达式的属性。包括让您在整个应用中重复使用复杂的布局

**5.1 Imports**

导入允许您轻松引用布局文件中的类，就像在托管代码中一样。import可以在data 元素内使用零个或多个元素。以下代码示例将View类导入布局文件：

```java
<data>
    <import type="android.view.View"/>
</data>
```

导入的View类，可以在绑定表达式中引用它。以下示例显示如何引用View类的常量VISIBLE和GONE：

```java
<TextView
   android:text="@{user.lastName}"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:visibility="@{user.isAdult ? View.VISIBLE : View.GONE}"/>
```

**5.2 Type aliases(类型别名)**

当存在类名冲突时，可以将其中一个类重命名为别名。以下示例将包中的View类 重命名com.example.real.estate为Vista：
	
```java
<import type="android.view.View"/>
<import type="com.example.real.estate.View"
        alias="Vista"/>
```

这样，可以用Vista来引用com.example.real.estate.View，而View引用android.view.View



.

.


# [4.Lifecycles](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)

### 介绍：

> Lifecycles是一个生命周期感知组件，当Activity或者Fragment的生命周期发生改变的时会，Lifecycles也会做出相应的生命周期状态的改变，它保存关于组件生命周期状态的信息(比如活动或片段)，并允许其他对象观察这种状态。

### 作用：

> 使用观察者模式追踪Activity或者Fragment的生命周期，以避免在Activity或者Fragment的生命周期方法中加入太多逻辑，降低代码耦合性，去除重复代码并且防止内存泄漏。


### 解决的问题：

>  **Lifecycle 的存在，主要是为了解决 生命周期管理 的一致性问题**

### Lifecycles使用

**1. 实现LifecycleObserver接口**

```java
public class MyObserver implements LifecycleObserver {

  private static final String TAG = "test";
	
  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  public void onCreate() {
    Log.d(TAG, "onCreate: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void onStart() {
    Log.d(TAG, "onStart: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void onResume() {
    Log.d(TAG, "onResume: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void onPause() {
    Log.d(TAG, "onPause: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  public void onStop() {
    Log.d(TAG, "onStop: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroy() {
    Log.d(TAG, "onDestroy: ");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
  public void onAny() {
    Log.d(TAG, "onAny: ");
  }
}
```

> 通过实现LifecycleObserver接口，然后在相应的方法上面添加注解@OnLifecycleEvent(Lifecycle.Event.XXX)即可。实际上，这就是一个观察者。当执行到某个生命周期时，会通知观察者执行对应的方法


**2.Activity中添加观察者**

```java
public class TestActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_activity);

	//添加观察者
    getLifecycle().addObserver(new MyObserver());

  }
}
```

> 继承AppCompatActivity后，即可通过添加观察者来监听此Activity的生命周期了。


**3.Fragment中添加观察者**

```java
public class TestFragment extends Fragment {
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

	//添加观察者
    getLifecycle().addObserver(new MyObserver());

  }
}
```



### Lifecycle中相关类和方法的说明

**1. Lifecycle类**

>Lifecycle类持有Activity 或 Fragment等组件的生命周期信息，并且允许其他对象观察这些信息。Lifecycle内部使用了两个枚举来跟踪其关联组件的生命周期状态：Event和State

可以通过调用Lifecycle类的 addObserver() 方法来添加观察者，如下：

```java
getLifecycle().addObserver(new MyObserver());
```

.

**2. Lifecycle事件**

Lifecycle组件可以感知如下事件：

```java
public abstract class Lifecycle {
  public enum Event {

    ON_CREATE,ON_START,ON_RESUME,ON_PAUSE,ON_STOP,ON_DESTROY,ON_ANY
  
	}
}
```
> 注：
>看起来有7种，实际上也就是6种而已。ON_ANY表示所有的事件都会感知。可以看到，像Activity的onRestart() ，Fragment的onCreateView()等等其他生命周期是无法感知的。

.

**3.Lifecycle状态**

Lifecycle组件内部维护了一个State来标识Activity或Fragment当前的状态。如下：

```java
public abstract class Lifecycle {
  public enum State {
    DESTROYED,
    INITIALIZED,
    CREATED,
    STARTED,
    RESUMED;

    public boolean isAtLeast(@NonNull State state) {
      return compareTo(state) >= 0;
    }
  }
}
```

一共也就5种状态而已，其状态和事件的状态关系如下图所示:

![](https://img-blog.csdnimg.cn/img_convert/f9655eb98b4b0842935cf24a724e4d19.png#pic_center)


**4.LifecycleOwner接口**

>- LifecycleOwner表示它的实现类具有一个 Lifecycle。它有一个 getLifecycle()方法，该方法必须由实现类实现。
>
>- AppCompatActivity和Fragment都实现了LifecycleOwner接口（Support Library 26.1.0之后的版本），所以可以直接拿来使用。
>
>- 但是Activity类并没有实现LifecycleOwner接口，所以，如果我们需要去监听自定义Activity的话，需要自己手动去实现LifecycleOwner接口。可以看前面的例子实现

.


**5.自定义Activity中实现LifecycleOwner**

> 感知AppCompatActivity和Fragment的子类生命周期都很简单，一行代码就可以完成了。但是感知自定义Activity就稍微复杂了点，实现我们手动去完成以下步骤：
>
>- 实现LifecycleOwner接口
>
>- 重写getLifecycle()方法
>
>- 手动标记生命周期的状态

其代码如下所示：

```java
public class TestActivity extends Activity implements LifecycleOwner {
  private LifecycleRegistry mLifecycleRegistry;

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    //返回Lifecycle
    return mLifecycleRegistry;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_activity);
    //创建Lifecycle对象
    mLifecycleRegistry = new LifecycleRegistry(this);
    //标记状态
    mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    //添加观察者
    getLifecycle().addObserver(new MyObserver());
  }
  
  @Override
  public void onStart() {
    super.onStart();
    //标记状态
    mLifecycleRegistry.markState(Lifecycle.State.STARTED);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //标记状态
    mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //标记状态
    mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
  }
}
```

上面的LifecycleRegistry其父类是Lifecycle

**参考资料：**

- [Android框架组件Lifecycle的使用](https://www.jb51.net/article/144759.htm)
- [Lifecycle官网](https://developer.android.google.cn/topic/libraries/architecture/lifecycle#java)


.

.

# [5.Room 持久性库 ](https://developer.android.google.cn/training/data-storage/room)

### 介绍：

> **Room 持久性库在 SQLite 的基础上提供了一个抽象层，让用户能够在充分利用 SQLite 的强大功能的同时，获享更强健的数据库访问机制。**

**作用：**
> 该库可帮助您在运行应用的设备，处理大量结构化数据的程序，可以从本地数据持久化中获取巨大利益。最常见的用例是缓存相关的数据。此缓存充当应用的单一可信来源，使用户能够在应用中查看关键信息的一致副本，无论用户是否具有互联网连接，在这种情况下，当设备无法访问网络的时候，用户仍然可以在离线时浏览内容。任何用户原始数据的变化都会在连接网络后同步。

### Room的组成：

>**Room有三个主要的组件：**
> .
> **1. 数据库（Database）**：包含数据库持有者，并作为应用已保留的持久关系型数据的底层连接的主要接入点。
> 
> 使用 @Database 注释的类应满足以下条件：
> 
> - 是扩展 RoomDatabase 的抽象类。
> - 在注释中添加与数据库关联的实体列表。
> -  包含具有 0 个参数且返回使用 @Dao 注释的类的抽象方法。
> 
> 在运行时，您可以通过调用 Room.databaseBuilder() 或 Room.inMemoryDatabaseBuilder()
> 获取 Database 的实例。
> .
>  **2. 实体（Entity）**：表示持有数据库表记录的类。对每种实体来说，创建了一个数据库表来持有所有项。你必须通过Database中的entities数组来引用实体类。实体的每个成员变量都被持久化在数据库中，除非你注解其为@Ignore。
> .
> **3. 数据访问对象（DAO）**：包含用于访问数据库的方法类或者接口。DAO是Room的主要组件，负责定义访问数据库的方法。被注解@Database的类必须包含一个无参数的抽象方法并返回被@Dao注解的类型。当编译时生成代码时，Room会创建该类的实现。


### Room的工作流程：

> 应用使用 Room 数据库来获取与该数据库关联的数据访问对象 (DAO)。然后，应用使用每个 DAO
> 从数据库中获取实体，然后再将对这些实体的所有更改保存回数据库中。 最后，应用使用实体来获取和设置与数据库中的表列相对应的值。


Room 不同组件之间的关系如图：

![](https://img-blog.csdnimg.cn/img_convert/45439707c867d0ac56416aea44494ab2.png#pic_center)


以下代码段包含具有一个实体和一个 DAO 的示例数据库配置：

**User.java**（数据库的表）

```java
@Entity
public class User {
	//把Id设置为主键
    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;
}
```


**UserDao.java**（数据库的操作方法）

```java
@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND "
           + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
```


**AppDatabase.java（数据库持有者）**

```java
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```

**创建上面的文件后，使用以下代码获取创建的数据库实例：**

```java
AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "database-name").build();
```


> **注意**：
> - 在实例化AppDatabase对象时应该遵循单例设计模式，因为每个 RoomDatabase 实例都相当昂贵，而您几乎不需要在单个进程中访问多个实例。
>
>-  如果您的应用在多个进程中运行，请在数据库构建器调用中包含 enableMultiInstanceInvalidation()。这样，如果您在每个进程中都有一个 AppDatabase 实例，可以在一个进程中使共享数据库文件失效，并且这种失效会自动传播到其他进程中 AppDatabase 的实例。


### Room的使用介绍：


使用 Room，请将以下依赖项添加到应用的 build.gradle 文件:

```java
dependencies {
  def room_version = "2.2.5"

  implementation "androidx.room:room-runtime:$room_version"
  annotationProcessor "androidx.room:room-compiler:$room_version" // For Kotlin use kapt instead of annotationProcessor

  // optional - Kotlin Extensions and Coroutines support for Room
  implementation "androidx.room:room-ktx:$room_version"

  // optional - RxJava support for Room
  implementation "androidx.room:room-rxjava2:$room_version"

  // optional - Guava support for Room, including Optional and ListenableFuture
  implementation "androidx.room:room-guava:$room_version"

  // Test helpers
  testImplementation "androidx.room:room-testing:$room_version"
}
```
    

**1. 使用Room entities定义数据**

> **包含：**
> - 使用主键
> 
> - 注释索引和唯一性
> 
> - 定义对象之间的关系
> 
> - 创建嵌套的对象

使用 Room持久性库时，可以将相关字段集合定义为实体。对于每个实体，在关联的[Database](https://developer.android.google.cn/reference/android/arch/persistence/room/Database.html)对象内创建一个表来保存这些项目。

默认情况下，Room会为实体中定义的每个字段创建一个列。如果实体具有不想保留的字段，可以使用[@Ignore](https://developer.android.google.cn/reference/android/arch/persistence/room/Ignore.html)注释。您必须通过[entities](https://developer.android.google.cn/reference/android/arch/persistence/room/Database.html#entities())数组在Database类中的引用实体类。

以下代码片段显示了如何定义一个实体：

```java
@Entity
class User {
    @PrimaryKey//主键
    public int id;

    public String firstName;
    public String lastName;

    @Ignore
    Bitmap picture;
}
```

去访问一个字段，Room必须有权限访问它。你可以公开一个字段，或者你可以为它提供一个getter和setter。如果您使用getter和setter方法，请记住它们基于Room中的JavaBeans约定。

> **注意：** 实体可以有一个空的构造函数（如果相应的DAO类可以访问每个持久化字段）或者一个构造函数的参数包含与实体中的字段类型和名称相匹配的类型和名称。房间也可以使用全部或部分构造函数，例如只接收一些字段的构造函数。

.

## 使用主键

> 每个实体必须至少定义一个字段作为主键。<即使只有1个字段，仍然需要使用注释对字段进行[@PrimaryKey](https://developer.android.google.cn/reference/android/arch/persistence/room/PrimaryKey.html)注释。另外，如果你想室自动分配ID的实体，您可以设置@PrimaryKeyautoGenerate属性。如果实体具有复合主键，则可以使用[@Entity](https://developer.android.google.cn/reference/android/arch/persistence/room/Entity.html)注释的primaryKeys 属性。

**如以下代码片段所示：**

```java
@Entity(primaryKeys = {"firstName", "lastName"})
class User {
    public String firstName;
    public String lastName;

    @Ignore
    Bitmap picture;
}
```

> **注：**
> 默认情况下，Room使用类名作为数据库表名。如果您希望表具有不同的名称，请设置@Entity注释的 tableName属性
> 如下面的代码片段所示：

	
```java
//SQLite中的表名是不区分大小写的
@Entity(tableName = "users")
class User {
    ...
}
```


> 与该tableName 属性类似,Room使用字段名称作为数据库中的列名称。如果您希望列的名称不同，请将[@ColumnInfo](https://developer.android.google.cn/reference/android/arch/persistence/room/ColumnInfo.html)注释添加到字段中
> 如以下代码片段所示：

```java
@Entity(tableName = "users")
class User {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}
```

.

## 忽略字段

> 默认情况下，Room 会为实体中定义的每个字段创建一个列。如果某个实体中有您不想保留的字段，则可以使用[@Ignore](https://developer.android.google.cn/reference/androidx/room/Ignore) 为这些字段添加注释

如以下代码段所示：

```java
@Entity
public class User {
    @PrimaryKey
    public int id;

    public String firstName;
    public String lastName;

    @Ignore
    Bitmap picture;
}
```

 
如果实体继承了父实体的字段，则使用 @Entity 属性的 ignoredColumns 属性通常会更容易：

```java
@Entity(ignoredColumns = "picture")
public class RemoteUser extends User {
    @PrimaryKey
    public int id;

    public boolean hasVpn;
}
```
    
   

.

**注释索引和唯一性**

> 根据您访问数据的方式，您可能需要对数据库中的某些字段进行索引以加快查询速度。要将索引添加到实体，请在@Entity注释中包含indices属性，列出要包含在索引或组合索引中的列的名称。

以下代码片段演示了这个注释过程：

```java
@Entity(indices = {@Index("name"),
        @Index(value = {"last_name", "address"})})
class User {
    @PrimaryKey
    public int id;

    public String firstName;
    public String address;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}
```


> 有时，数据库中的某些字段或字段组必须是唯一的。您可以通过强行设置[@Index](https://developer.android.google.cn/reference/android/arch/persistence/room/Index.html)注释的unique
> 属性为true来确保唯一性。以下代码示例可防止表中有两列包含与firstName lastName列相同的一组值

```java
@Entity(indices = {@Index(value = {"first_name", "last_name"},
        unique = true)})
class User {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @Ignore
    Bitmap picture;
}
```
.

**2. 使用Room DAO访问数据**

>如需使用 Room 持久性库访问应用的数据，您可以使用数据访问对象 (DAO)。这些 Dao 对象构成了 Room 的主要组件，因为每个 DAO 都包含一些方法，这些方法提供对应用数据库的抽象访问权限。DAO 既可以是接口，也可以是抽象类。如果是抽象类，则该 DAO 可以选择有一个以 RoomDatabase 为唯一参数的构造函数。Room 会在编译时创建每个 DAO 实现。

### 插入

> 当您创建 DAO 方法并使用 @Insert 对其进行注释时，Room 会生成一个实现，该实现在单个事务中将所有参数插入数据库中

以下代码段展示了几个示例的插入：

```java
@Dao
public interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(User... users);

    @Insert
    public void insertBothUsers(User user1, User user2);

    @Insert
    public void insertUsersAndFriends(User user, List<User> friends);
}
```

> 如果 @Insert 方法只接收 1 个参数，则它可以返回 long，这是插入项的新 rowId。如果参数是数组或集合，则应返回
> long[] 或 List<Long>。

  


### 更新

>[@Update](https://developer.android.google.cn/reference/androidx/room/Update)：便捷方法会修改数据库中以参数形式给出的一组实体。它使用与每个实体的主键匹配的查询

以下代码段演示了如何定义此方法：

```java
@Dao
public interface MyDao {
    @Update
    public void updateUsers(User... users);
}
```
> **注：**
>虽然通常没有必要，但是您可以让此方法返回一个 int 值，以指示数据库中更新的行数。


### 删除

> [@Delete](https://developer.android.google.cn/reference/androidx/room/Delete)：便捷方法会从数据库中删除一组以参数形式给出的实体。它使用主键查找要删除的实体

以下代码段演示了如何定义此方法：

```java
@Dao
public interface MyDao {
    @Delete
    public void deleteUsers(User... users);
}
```
> **注：**
> 虽然通常没有必要，但是您可以让此方法返回一个 int 值，以指示从数据库中删除的行数。


### 查询信息

##### 简单查询

```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM user")
    public User[] loadAllUsers();
}
```
    

> 这是一个极其简单的查询，可加载所有用户。在编译时，Room 知道它在查询用户表中的所有列。如果查询包含语法错误，或者数据库中没有用户表格，则
> Room 会在您的应用编译时显示包含相应消息的错误。


##### 将参数传递给查询

> 在大多数情况下，您需要将参数传递给查询以执行过滤操作，例如仅显示某个年龄以上的用户。要完成此任务，请在 Room 注释中使用方法参数

如以下代码段所示：

```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM user WHERE age > :minAge")
    public User[] loadAllUsersOlderThan(int minAge);
}
```

> 在编译时处理此查询时，Room 会将 :minAge 绑定参数与 minAge 方法参数进行匹配。Room
> 通过参数名称进行匹配。如果有不匹配的情况，则应用编译时会出现错误。

**您还可以在查询中传递多个参数或多次引用这些参数** 
如以下代码段所示：

```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
    public User[] loadAllUsersBetweenAges(int minAge, int maxAge);

    @Query("SELECT * FROM user WHERE first_name LIKE :search " +
           "OR last_name LIKE :search")
    public List<User> findUserWithName(String search);
}
```
    


##### 返回列的子集

> 大多数情况下，您只需获取实体的几个字段。例如，您的界面可能仅显示用户的名字和姓氏，而不是用户的每一条详细信息。通过仅提取应用界面中显示的列，您可以节省宝贵的资源，并且您的查询也能更快完成。

借助 Room，您可以从查询中返回任何基于 Java 的对象，前提是结果列集合会映射到返回的对象。例如，您可以创建以下基于 Java 的普通对象 (POJO) 来获取用户的名字和姓氏：

```java
public class NameTuple {
    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    @NonNull
    public String lastName;
}
```

现在，您可以在查询方法中使用此 POJO：

```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user")
    public List<NameTuple> loadFullName();
}
```
    
>Room 知道该查询会返回 first_name 和 last_name 列的值，并且这些值会映射到 NameTuple 类的字段中。因此，Room 可以生成正确的代码。如果查询返回的列过多，或者返回 NameTuple 类中不存在的列，则 Room 会显示一条警告。

.

##### 传递参数的集合

> 您的部分查询可能要求您传入数量不定的参数，参数的确切数量要到运行时才知道。例如，您可能希望从部分区域中检索所有用户的相关信息。
> Room 知道参数何时表示集合，并根据提供的参数数量在运行时自动将其展开。

```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
    public List<NameTuple> loadUsersFromRegions(List<String> regions);
}
```
    

##### 查询多个表格

> 您的部分查询可能需要访问多个表格才能计算出结果。借助 Room，您可以编写任何查询，因此您也可以联接表格。此外，如果响应是可观察数据类型（如 Flowable 或 LiveData），Room 会观察查询中引用的所有表格，以确定是否存在无效表格。

以下代码段展示了如何执行表格联接以整合以下两个表格的信息：一个表格包含当前借阅图书的用户，另一个表格包含当前处于已被借阅状态的图书的数据。

```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM book " +
           "INNER JOIN loan ON loan.book_id = book.id " +
           "INNER JOIN user ON user.id = loan.user_id " +
           "WHERE user.name LIKE :userName")
   public List<Book> findBooksBorrowedByNameSync(String userName);
}
```

您还可以从这些查询中返回 POJO。例如，您可以编写一条加载某位用户及其宠物名字的查询，如下所示：

```java
@Dao
public interface MyDao {
   @Query("SELECT user.name AS userName, pet.name AS petName " +
          "FROM user, pet " +
          "WHERE user.id = pet.user_id")
   public LiveData<List<UserPet>> loadUserAndPetNames();

   static class UserPet {
       public String userName;
       public String petName;
   }
}
```

 
   
##### 使用 LiveData 进行可观察查询

> 执行查询时，您通常会希望应用的界面在数据发生变化时自动更新。为此，请在查询方法说明中使用 LiveData
> 类型的返回值。当数据库更新时，Room 会生成更新 LiveData 所必需的所有代码。
>**注意：**
>自版本 1.0 起，Room 会根据在查询中访问的表格列表决定是否更新 LiveData 实例。

```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
    public LiveData<List<User>> loadUsersFromRegionsSync(List<String> regions);
}
```

.

.

### [Demo项目地址](https://gitee.com/qu-wenbin/my-jetpack-demo)
### [Room的Demo项目地址](https://gitee.com/qu-wenbin/room-demo)

 
