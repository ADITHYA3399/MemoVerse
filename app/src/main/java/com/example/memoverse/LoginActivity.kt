package com.example.memoverse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.Icon
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
//import androidx.compose.material.icons.filled.Visibility  // ok
//import androidx.compose.material.icons.filled.VisibiltyOff // ok
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.memoverse.ui.theme.MemoVerseTheme
import kotlin.text.Typography

class LoginActivity : ComponentActivity() {
    private lateinit var databaseHelper: UserDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = UserDatabaseHelper(this)



        setContent {
            MemoVerseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(context = this, databaseHelper = databaseHelper)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(context: Context, databaseHelper: UserDatabaseHelper) {
    var _passwordVisible = false
//    Image(
//        painterResource(id = R.drawable.memoverseloginbg), contentDescription = "",
//        contentScale = ContentScale.FillBounds,
//        )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val pontano = FontFamily(
        Font(R.font.pontanosansregular),
        Font(R.font.pontanosanbold, FontWeight.Bold)
    )
    val Typography = Typography(
        h1 = TextStyle(
            fontFamily = pontano,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        body1 = TextStyle(
            fontFamily = pontano,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.h1,
            color = colorResource(id = R.color.mainbg),
            text = "Sign-In"
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(value = username, onValueChange = {username = it},
            label = { Text("Username", style = MaterialTheme.typography.body1)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = {Icon(Icons.Outlined.Person, contentDescription = null)},
            modifier = Modifier
                .width(280.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.mainbg),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.mainbg),
                unfocusedLabelColor = Color.Gray,
                leadingIconColor = colorResource(id = R.color.mainbg),
                textColor = Color.Black
            )

        )
        var showPassword by remember { mutableStateOf(value = false) }
        OutlinedTextField(value = password, onValueChange = {password = it},
            label = { Text("Password", style = MaterialTheme.typography.body1)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {Icon(Icons.Outlined.Lock, contentDescription = null)},
            modifier = Modifier
                .width(280.dp),
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.mainbg),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.mainbg),
                unfocusedLabelColor = Color.Gray,
                leadingIconColor = colorResource(id = R.color.mainbg),
                textColor = Color.Black,
                trailingIconColor = colorResource(id = R.color.mainbg)
            ),
            trailingIcon = {
                if (showPassword) {
                    TextButton(onClick = { showPassword = false }) {
                        Text(text = "hide", color = colorResource(id = R.color.mainbg))
                    }
                } else {
                    TextButton(onClick = { showPassword = true }) {
                        Text(text = "show", color = colorResource(id = R.color.mainbg))
                    }
                }
            }
        )


        Spacer(modifier = Modifier.height(30.dp))
        val mContext = LocalContext.current


        Row {
            Button(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                },
                border = BorderStroke(1.dp, color = colorResource(id = R.color.mainbg)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .width(150.dp)
                    .height(40.dp)
            ) {
                Text(text = "Sign-Up",
                    color = colorResource(id = R.color.mainbg),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.body1)
            }
            Spacer(modifier = Modifier.width(30.dp))

            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val user = databaseHelper.getUserByUsername(username)
                        if (user != null && user.password == password) {
                            error = "Successfully log in"
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                            //onLoginSuccess()
                        }
                        else {
                            error =  "Invalid username or password"
                        }

                    } else {
                        error = "Please fill all fields"
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.mainbg)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .width(150.dp)
                    .height(40.dp)
            ) {
                Text(text = "Sign-In",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Forget password?", color = Color.White)
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "", color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    MemoVerseTheme {
        LoginActivity()
    }
}