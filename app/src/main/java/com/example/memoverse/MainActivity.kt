package com.example.memoverse

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoverse.ui.theme.MemoVerseTheme
import java.io.ByteArrayInputStream
import java.util.Calendar
import java.util.Date


private var DATE_ENTERED : String? = null
private lateinit var SELECTED_DATE : String
var ARRAY_NOTES by mutableStateOf(mutableStateListOf<Note>())
private lateinit var note_database_helper : NoteDatabaseHelper

class MainActivity : ComponentActivity() {
    private lateinit var databaseHelper: NoteDatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = NoteDatabaseHelper(this)
        note_database_helper = NoteDatabaseHelper(this@MainActivity)
        setContent {
            MemoVerseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.top2)
                ) {
                    var mDate = remember { mutableStateOf("") }
                    val scaffoldState = rememberScaffoldState()
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    Scaffold(
                        scaffoldState = scaffoldState,
                        floatingActionButton = {
                            Column (modifier = Modifier){
                                addFab()
                                Spacer(modifier = Modifier.padding(5.dp))
                                dateFab()
                                //ExpandableFab(context = this@MainActivity)
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "MemoVerse") },
                                backgroundColor = colorResource(id = R.color.top1)
                            )
                        }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Black)) {
                            //Datepicking()

                            var notesList : ArrayList<Note>? = note_database_helper.getNoteByDate("2023-07-02")
                            ARRAY_NOTES.clear()
                            if (notesList != null) {
                                ARRAY_NOTES.addAll(notesList)
                            }
                            LazyColumn{
                                items(ARRAY_NOTES) { note ->
                                    var imagebytearray = note?.image?.let { it1 -> byteArrayToImageBitmap(byteArray = it1) }
                                    if (imagebytearray != null) {
                                        note.summary?.let { it1 -> note.title?.let { it2 -> ImageCard(title = it2, summary = it1, Image = imagebytearray) } }
                                    }
                                }
                            }

//                            var imagebytearray = note?.image?.let { it1 -> byteArrayToImageBitmap(byteArray = it1) }
//
//                            if (note != null) {
//                                note.title?.let { it1 -> note.summary?.let { it2 ->
//                                    if (imagebytearray != null) {
//                                        ImageCard(title = it1, summary = it2, Image = imagebytearray)
//                                    }
//                                } }
//                            }

                        }
                    }
                }
            }
        }
    }

}


@Composable
fun ExpandableFab(context: Context) {
    var isExpanded by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { isExpanded = !isExpanded }
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
            contentDescription = "Expandable FAB"
        )
    }

    Box(modifier = Modifier.padding(top = 16.dp)) {
        AnimatedVisibility(visible = isExpanded) {
            Column(Modifier.align(Alignment.TopCenter)) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "Add new note", color = Color.Black)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_note_add_24),
                            contentDescription = "Navigate FAB",
                            tint = Color.Black,
                        )
                    },
                    onClick = {

                    },
                    backgroundColor = colorResource(id = R.color.top3)
                )

                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "Select Date", color = Color.Black)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_date_range_24),
                            contentDescription = "Navigate FAB",
                            tint = Color.Black,
                        )
                    },
                    onClick = {

                    },
                    backgroundColor = colorResource(id = R.color.top3)
                )
            }
        }
    }
}


@Composable
fun ImageCard(
    title: String,
    summary: String,
    Image: ImageBitmap,
    modifier: Modifier = Modifier
){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(9.dp),
        elevation = 5.dp
    ){
        Box(modifier = Modifier
            .height(200.dp)
            .background(color = colorResource(id = R.color.top4))) {
            Column {
                Text(title, style = TextStyle(color = Color.White, fontSize = 20.sp),modifier = Modifier.padding(5.dp))
                Text(summary, style = TextStyle(color = Color.White, fontSize = 16.sp),modifier = Modifier.padding(5.dp))

                Image(bitmap = Image,
                    contentDescription = "image",
                    contentScale = ContentScale.FillWidth, modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Column {
        note.title?.let { Text(text = it) }
        note.summary?.let { Text(text = it) }
        note.image?.let { imageByteArray ->
            byteArrayToImageBitmap(byteArray = imageByteArray)?.let {
                Image(bitmap = it,
                    contentDescription = "Note Image")
            }
        }
    }
}

@Composable
fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap? {
    val inputStream = ByteArrayInputStream(byteArray)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    return bitmap?.asImageBitmap()
}

var datesending = ""
@Composable
fun Datepicking(){
    val mContext = LocalContext.current
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mCalendar = Calendar.getInstance()
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()
    val mDate = remember { mutableStateOf("") }
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mYear-${mMonth+1}-$mDayOfMonth"
        }, mYear, mMonth, mDay
    )
    DATE_ENTERED = mDate.toString()
    mDatePickerDialog.datePicker.maxDate = mCalendar.timeInMillis

    Column(horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { mDatePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                Text(text = "Pick a date", color = Color.Black)
            }

            IconButton(onClick = {
                //mDatePickerDialog.show()
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_calendar_month_24), contentDescription = "calender")
            }
        }
        datesending = mDate.value
        Text(text = "${mDate.value}", fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}
//@Composable
//fun stringOnDate(mDate: MutableState<String>): String {
//    if(mDate.equals("")) return "Pick a date"
//    else return mDate
//}
@Composable
fun addFab(){
    val context = LocalContext.current
    ExtendedFloatingActionButton(
        text = {
            Text(text = "Add a note", color = Color.Black)
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_note_add_24),
                contentDescription = "Navigate FAB",
                tint = Color.Black,
            )
        },
        onClick = {
            var currentDate : String? = getCurrentDate()
            val intent = Intent(context, NoteAdd::class.java)
            intent.putExtra("Datesending", currentDate)
            context.startActivity(intent)
        },
        backgroundColor = colorResource(id = R.color.top3)
    )
}


@Composable
fun dateFab(){
    val context = LocalContext.current

    val mContext = LocalContext.current
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mCalendar = Calendar.getInstance()
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()
    val mDate = remember { mutableStateOf("") }
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mYear-${mMonth+1}-$mDayOfMonth"
        }, mYear, mMonth, mDay
    )

    SELECTED_DATE = mDate.toString()
    mDatePickerDialog.datePicker.maxDate = mCalendar.timeInMillis

    ExtendedFloatingActionButton(
        text = {
            Text(text = "Select Date", color = Color.Black)
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_date_range_24),
                contentDescription = "Navigate FAB",
                tint = Color.Black,
            )
        },
        onClick = {
            mDatePickerDialog.show()
            updateNoteList(SELECTED_DATE)
        },
        backgroundColor = colorResource(id = R.color.top3)
    )
}

fun updateNoteList( SELECTED_DATE: String) {
    var notesList : ArrayList<Note>? = note_database_helper.getNoteByDate(SELECTED_DATE)
    if (notesList != null) {
        ARRAY_NOTES.clear()
        ARRAY_NOTES.addAll(notesList)
    }
}


//@Composable
//fun dateFab() {
//    val context = LocalContext.current
//
//    ExtendedFloatingActionButton(
//        text = {
//            Text(text = "Pick a Date", color = Color.Black)
//        },
//        icon = {
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_date_range_24),
//                contentDescription = "Navigate FAB",
//                tint = Color.Black,
//            )
//        },
//        onClick = {
//
//        },
//        backgroundColor = Color.White
//    )
//}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MemoVerseTheme {
    }
}