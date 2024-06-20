package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column {

                    MainContent()
                }

            }



        }
    }
}
@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ){
            content()


        }
    }

}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .height(150.dp)
//            .clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,

            )
            Text(text = "$$total",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold

                )

        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent() {
    val splitByState = remember { mutableStateOf(1) }
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember { mutableStateOf(0.0) }
    val totalPerPersonState = remember { mutableStateOf(0.0) }

    Column(modifier = Modifier.padding(all = 12.dp)) {


        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState,
        ){}
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {},
){
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember { mutableStateOf(0f) }

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                enabled = true,
                labelId = "Enter Bill",
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if (validState){
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = modifier.width(120.dp))
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.value > 1) {
                                    splitByState.value -= 1
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0,
                                        splitBy = splitByState.value,
                                        tipPercentage = (sliderPositionState.value * 100).toInt()
                                    )
                                }
                            }
                        )
                        Text(
                            text = "${splitByState.value}",
                            modifier.align(alignment = Alignment.CenterVertically).padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value += 1
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0,
                                        splitBy = splitByState.value,
                                        tipPercentage = (sliderPositionState.value * 100).toInt()
                                    )
                                }
                            }
                        )
                    }
                }

                // Tip Row
                Row(
                    modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "Tip",
                        modifier = modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = modifier.width(200.dp))
                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = modifier.align(Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${(sliderPositionState.value * 100).toInt()} %")
                    Spacer(modifier = modifier.height(14.dp))
                    // Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            val newTipPercentage = (newVal * 100).toInt()
                            tipAmountState.value = calculateTotalTip(
                                totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0,
                                tipPercentage = newTipPercentage
                            )
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0,
                                splitBy = splitByState.value,
                                tipPercentage = newTipPercentage
                            )
                        },
                        modifier = modifier.padding(start = 16.dp, end = 16.dp)
                    )
                }
            } else {
                Box(){

                }}

        }


    }
}





