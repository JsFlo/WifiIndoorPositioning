# Goal
To provide indoor position tracking using a set of WiFi signals and Machine Learning.

## Simple Use Case:

  User has a set of wifi signal strengths (RSSI) and would like to know in what
  room he is in.

  * **input**: a **set** of wifi **signals** *(ex: [-73, -32, -67, ...])*
  * **output**: a **single value** that will be **mapped** to a **label** *(ex: 3, 3='Living Room')*

### Overview
* [Data Collection](#header_data_collection)
  * Collect **training data** using a **sensor** (*Android Device*)
* [Data Cleaning](#header_data_cleaning)
  * **Filter** the **training data** to include only a **subset** of *stable or frequent* **features** (*only use the access points that are consistently there*)
  * Split up the data into two sets:
    * train data used for training
    * validation data to test after training
* [Training](#header_training)
  * Use the **filtered training data** to **train** a **Neural Network** (*using TensorFlow*) model
* [Testing](#header_testing)
  * **Test** the **trained** model with a **validation set** collected at the time of training
  * **Test** the model in **real world live data** (*Using an Android app*)
* [Notes](#header_notes)

## Data Collection<a name="header_data_collection"></a>
I created an app(*AndroidWifiDataCollector*) to collect wifi data.
It captures the **BSSID** to use as a **unique identifier**,the **level** (*RSSI*) and a **label** (*int*)

In this phase we are **collecting training samples** the **label** must be **specified** by someone (*Supervised Learning!*).

<img src="/images/wifi_data_collector.png" width="200" height="400" />

**User Interaction**:
* Start Button
  * Start **collecting data** using the **label** specified in the **Spinner**
* Stop Button
  * Stop **collecting data**
* Label Spinner
  * **label** corresponding to a **number** that will be used in the **training data gathered**

#### Training Data output
  There is **no in-app filtering** of the source of data by **design**. There are **many access points** that only show up in a **few training examples** but **instead** of filtering in-app and **losing that data**. I wanted to **save it** in case I wanted to work on a separate machine learning project that would look into working with an input feature set that has missing data (0's for some inputs).

  Here is an example of the training data that would come from the app. (*Not real data*)


```text
+---------------+---------------------------------+
| Training Rows | Access Points                   |
+               +---------------------------------+
|               | A1  | A2  | A3  | A4  | A5 | A6 |
+---------------+-----+-----+-----+-----+----+----+
| Row 1         | -98 | -54 | 0   | -6  | -9 | -5 |
+---------------+-----+-----+-----+-----+----+----+
| Row 2         | -45 | -64 | -45 | -8  | 0  | 0  |
+---------------+-----+-----+-----+-----+----+----+
| Row 3         | -67 | -78 | -34 | -26 | -8 | 0  |
+---------------+-----+-----+-----+-----+----+----+
```
  We **want to** only work with a **smaller subset** of access points that show up **always or very frequently**. So in the above example we would only select **A1, A2, A4** because they had **values** in **all** the **training** samples (*3 rows in example*

  This filtering will be done in the next step .

## Data Cleaning<a name="header_data_cleaning"></a>

The goal in this section is to end up with 2 sets and 4 files.

Sets:
1. Training data (**80%** of the data collected)
  * features: train_data_features
  * labels: train_data_labels
2. Test Data (**20%**)
  * features: test_data_features
  * labels: test_data_labels

### How
```text
+---------------+---------------------------------+
| Training Rows | Access Points                   |
+               +---------------------------------+
|               | A1  | A2  | A3  | A4  | A5 | A6 |
+---------------+-----+-----+-----+-----+----+----+
| Row 1         | -98 | -54 | 0   | -6  | -9 | -5 |
+---------------+-----+-----+-----+-----+----+----+
| Row 2         | -45 | -64 | -45 | -8  | 0  | 0  |
+---------------+-----+-----+-----+-----+----+----+
| Row 3         | -67 | -78 | -34 | -26 | -8 | 0  |
+---------------+-----+-----+-----+-----+----+----+
```
The **data collected** in the **last phase** collected a bunch of wifi strengths from some access points that are **not always there** (0 values).

I decided to **handpick** the the **access points** based on the **number of times** the access points **shows up in the data**.

I created a **map** that mapped the access point and the number of times they showed up:
```text
A1: 900
A2: 850
A3: 450
A4: 800
```
In the example above **I would select A1, A2, A4** because they show up the most.

### Filtering
**After handpicking** the access points I **filtered out** the data that did not have a **value for each**.

Sample Output:

#### test|train_data_features:

```text
-56, -23, -89
-46, -25, -68
```
#### test|train_data_labels:
```text
1, 0, 0
0, 1, 0
```

## Training<a name="header_training"></a>
## Testing<a name="header_testing"></a>
## Notes<a name="header_notes"></a>
