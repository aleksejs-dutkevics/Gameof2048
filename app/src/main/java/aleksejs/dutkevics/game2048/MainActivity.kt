package aleksejs.dutkevics.game2048

import aleksejs.dutkevics.game2048.GameData.bottomSwipe
import aleksejs.dutkevics.game2048.GameData.cantMoveDown
import aleksejs.dutkevics.game2048.GameData.cantMoveLeft
import aleksejs.dutkevics.game2048.GameData.cantMoveRight
import aleksejs.dutkevics.game2048.GameData.cantMoveUp
import aleksejs.dutkevics.game2048.GameData.cellsAmount
import aleksejs.dutkevics.game2048.GameData.firstLaunch
import aleksejs.dutkevics.game2048.GameData.gameOver
import aleksejs.dutkevics.game2048.GameData.grid
import aleksejs.dutkevics.game2048.GameData.leftSwipe
import aleksejs.dutkevics.game2048.GameData.playerName
import aleksejs.dutkevics.game2048.GameData.playerScore
import aleksejs.dutkevics.game2048.GameData.playerScored2048
import aleksejs.dutkevics.game2048.GameData.rightSwipe
import aleksejs.dutkevics.game2048.GameData.topSwipe
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import aleksejs.dutkevics.game2048.ui.theme.GameOf2048Theme
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
// Both are for val *variable* by .. remember { ... }
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        // This game is designed to be played in portrait mode only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)

        setContent {
            GameOf2048Theme {
                Surface {
                    ScreenLayout()
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLayout() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Score: ${playerScore.value}",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        GameField()

        val gameActive = !gameOver.value && !firstLaunch.value

        Spacer(modifier = Modifier.size(25.dp))

        // Control buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(modifier = Modifier.width(111.dp), enabled = !cantMoveLeft.value && gameActive, onClick = {
                leftSwipe()
            }) { Text("left", fontSize = 25.sp) }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(modifier = Modifier.width(100.dp), enabled = !cantMoveUp.value && gameActive, onClick = {
                    topSwipe()
                }) { Text("up", fontSize = 25.sp) }
                Spacer(modifier = Modifier.size(10.dp))
                Button(modifier = Modifier.width(120.dp), enabled = !cantMoveDown.value && gameActive, onClick = {
                    bottomSwipe()
                }) { Text("down", fontSize = 25.sp) }
            }

            Button(modifier = Modifier.width(111.dp), enabled = !cantMoveRight.value && gameActive, onClick = {
                rightSwipe()
            }) { Text("right", fontSize = 25.sp) }
        }
    }

    // First launch screen with name input
    if (firstLaunch.value) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0xFF131633).copy(alpha = .9f))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to 2048, ${playerName.value}!",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "What's your name?",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
                if(playerName.value.length > 10) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "Pardon me, can we shorten it a bit?",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))

                TextField(
                    maxLines = 1,
                    singleLine = true,
                    value = playerName.value,
                    onValueChange = {
                        playerName.value = it
                    }
                )

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = { startGame() }
                ) {
                    Text(text = "Start Game")
                }

            }
        }
    }

    // Screen overlay for win
    if(gameOver.value && playerScored2048.value) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0xFF131633).copy(alpha = .9f))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = """
                        Congratulations, ${playerName.value}!
                        
                        You got 2048 and scored
                      
                        ${playerScore.value}
                        point${if(playerScore.value/10 == 1) "" else "s"}!
                    """.trimIndent(),
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = { startGame() }
                ) {
                    Text(text = "Play Again")
                }
            }
        }
    }

    // Screen overlay for lost
    if(gameOver.value && !playerScored2048.value) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0xFF131633).copy(alpha = .9f))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = """
                        Well played, ${playerName.value}
                        
                        but you're out of moves!
                        
                        
                        You scored ${playerScore.value} point${if(playerScore.value/10 == 1) "" else "s"}!
                    """.trimIndent(),
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = { startGame() }
                ) {
                    Text(text = "Play Again")
                }
            }
        }
    }
}

@Composable
fun GameField() {
        LazyVerticalGrid(
            contentPadding = PaddingValues(5.dp),
            userScrollEnabled = false,
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .background(Color(0xFFBBAC9D))
/*
* There are gesture movements available, they appear to be very janky.
* I couldn't figure out any better option fast. yet. Sorry :(
*/
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        when {
                            // Right to Left drag
                            dragAmount.x in -70f..-60f -> {
                                leftSwipe()
                            }

                            // Left to Right drag
                            dragAmount.x in 60f..70f -> {
                                rightSwipe()
                            }

                            // Bottom to Top drag
                            dragAmount.y in -70f..-60f -> {
                                topSwipe()
                            }

                            // Top to Bottom drag
                            dragAmount.y in 60f..70f -> {
                                bottomSwipe()
                            }
                        }
                    }
                }
        ) {
            items(16) { cell ->
                SingleCell(grid[cell / 4][cell % 4].value)
            }
        }
}

@Composable
fun SingleCell(value: Int = 0) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .background(
                Color(
                    when (value) {
                        2 -> 0xFFEDE3D9
                        4 -> 0xFFEDE0C8
                        8 -> 0xFFF2B17A
                        16 -> 0xFFF59564
                        32 -> 0xFFF67C5F
                        64 -> 0xFFF65F3B
                        128 -> 0xFFECCF73
                        256 -> 0xFFECCB62
                        512 -> 0xFFECC850
                        1024 -> 0xFFECC43F
                        2048 -> 0xFFE9CD5D
                        else -> 0xFFCABFB0
                    }
                )
            )
    ) {
        if (value != 0) {
            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.toString(),
                    textAlign = TextAlign.Center,
                    style = when {
                        value < 100 -> MaterialTheme.typography.displayMedium
                        value < 1000 -> MaterialTheme.typography.displaySmall
                        else -> MaterialTheme.typography.headlineMedium
                    },
                    color = Color(if (value <= 4) 0xFF776E65 else 0xFFFBF5F0)
                )
            }
        }
    }
}

fun startGame() {
    firstLaunch.value = false
    playerScore.value = 0
    playerScored2048.value = false

    for (row in 0..3)
        for (col in 0..3)
            grid[row][col].value = 0

    gameOver.value = false
    cantMoveLeft.value = false
    cantMoveRight.value = false
    cantMoveUp.value = false
    cantMoveDown.value = false
    cellsAmount.value = 0

    // TODO Grants a win in one turn for the purpose of testing
//    grid[0][0].value = 1024
//    grid[0][1].value = 1024
//    grid[0][2].value = 512

    addCell()
}

fun addCell() {
    val row = (0..3).random()
    val col = (0..3).random()

    if (cellsAmount.value < 16) {
        if (grid[row][col].value == 0) {
            grid[row][col].value = (1..2).random() * 2
            cellsAmount.value++
        } else {
            addCell()
        }
    }
}

object GameData {
    val firstLaunch = mutableStateOf(true)
    val playerName = mutableStateOf("Player")

    val playerScore = mutableStateOf( 0 )
    val playerScored2048 = mutableStateOf( false )

    val grid = List(4) { MutableList(4) { mutableStateOf(0) } }
    val cellsAmount = mutableStateOf(0)

    val cantMoveLeft = mutableStateOf(false)
    val cantMoveRight = mutableStateOf(false)
    val cantMoveUp = mutableStateOf(false)
    val cantMoveDown = mutableStateOf(false)

    val gameOver = mutableStateOf(false)

    private var lastCellValue = 0
    private var lastCellPosition = 0

    fun leftSwipe() {
        // checks if any cells moved per turn in the end by comparing the game grid before/after
        cantMoveLeft.value = true
        val gridCopy = List(4) { MutableList(4) { 0 } }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                gridCopy[row][col] = grid[row][col].value
            }
        }

        for (row in grid.indices) {
            lastCellValue = grid[row][0].value
            lastCellPosition = 0

            for (col in 1 until grid[row].size) {
                // Skips any action of current cell is empty (a zero)
                if (grid[row][col].value == 0) continue

                // current and last cells are identical
                if (grid[row][col].value == lastCellValue) {
                    lastCellValue *= 2
                    grid[row][lastCellPosition].value = lastCellValue
                    grid[row][col].value = 0

                    playerScore.value += lastCellValue
                    if(lastCellValue == 2048) playerScored2048.value = true

                    lastCellValue = 0
                    lastCellPosition++
                    cellsAmount.value--

                    // Positions closest non-zero cells to left
                } else {
                    if (lastCellPosition == 3) {
                        break
                    }
                    lastCellValue = grid[row][col].value
                    grid[row][col].value = 0
                    if (grid[row][lastCellPosition].value == 0)
                        grid[row][lastCellPosition].value = lastCellValue
                    else
                        grid[row][++lastCellPosition].value = lastCellValue
                }
            }

        }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                if (gridCopy[row][col] != grid[row][col].value) cantMoveLeft.value = false
            }
        }
        if (!cantMoveLeft.value) {
            addCell()
            cantMoveRight.value = false
            cantMoveUp.value = false
            cantMoveDown.value = false
        }
        checkGameOver()

    }

    fun rightSwipe() {
        cantMoveRight.value = true
        val gridCopy = List(4) { MutableList(4) { 0 } }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                gridCopy[row][col] = grid[row][col].value
            }
        }

        for (row in grid.indices) {
            lastCellValue = grid[row][grid[row].size - 1].value
            lastCellPosition = grid[row].size - 1

            for (col in grid[row].size - 2 downTo 0) {
                // Skips any action of current cell is empty (a zero)
                if (grid[row][col].value == 0) continue

                // current and last cells are identical
                if (grid[row][col].value == lastCellValue) {
                    lastCellValue *= 2
                    grid[row][lastCellPosition].value = lastCellValue
                    grid[row][col].value = 0

                    playerScore.value += lastCellValue
                    if(lastCellValue == 2048) playerScored2048.value = true

                    lastCellValue = 0
                    lastCellPosition--
                    cellsAmount.value--

                    // Positions closest non-zero cells to left
                } else {
                    if (lastCellPosition == 0) break
                    lastCellValue = grid[row][col].value
                    grid[row][col].value = 0
                    if (grid[row][lastCellPosition].value == 0)
                        grid[row][lastCellPosition].value = lastCellValue
                    else
                        grid[row][--lastCellPosition].value = lastCellValue
                }
            }

        }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                if (gridCopy[row][col] != grid[row][col].value) cantMoveRight.value = false
            }
        }
        if (!cantMoveRight.value) {
            addCell()
            cantMoveLeft.value = false
            cantMoveUp.value = false
            cantMoveDown.value = false
        }
        checkGameOver()
    }

    fun topSwipe() {
        cantMoveUp.value = true
        val gridCopy = List(4) { MutableList(4) { 0 } }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                gridCopy[row][col] = grid[row][col].value
            }
        }

        for (col in grid.indices) {
            lastCellValue = grid[0][col].value
            lastCellPosition = 0

            for (row in 1 until grid.size) {
                // Skips any action of current cell is empty (a zero)
                if (grid[row][col].value == 0) continue

                playerScore.value += lastCellValue
                if(lastCellValue == 2048) playerScored2048.value = true

                // current and last cells are identical
                if (grid[row][col].value == lastCellValue) {
                    lastCellValue *= 2
                    grid[lastCellPosition][col].value = lastCellValue
                    grid[row][col].value = 0

                    lastCellValue = 0
                    lastCellPosition++
                    cellsAmount.value--

                    // Positions closest non-zero cells to left
                } else {
                    if (lastCellPosition == 3) break
                    lastCellValue = grid[row][col].value
                    grid[row][col].value = 0
                    if (grid[lastCellPosition][col].value == 0)
                        grid[lastCellPosition][col].value = lastCellValue
                    else
                        grid[++lastCellPosition][col].value = lastCellValue
                }
            }

        }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                if (gridCopy[row][col] != grid[row][col].value) cantMoveUp.value = false
            }
        }
        if (!cantMoveUp.value) {
            addCell()
            cantMoveLeft.value = false
            cantMoveRight.value = false
            cantMoveDown.value = false
        }
        checkGameOver()
    }

    fun bottomSwipe() {
        cantMoveDown.value = true
        val gridCopy = List(4) { MutableList(4) { 0 } }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                gridCopy[row][col] = grid[row][col].value
            }
        }

        for (col in grid.indices) {
            lastCellValue = grid[grid.size - 1][col].value
            lastCellPosition = grid.size - 1

            for (row in grid.size - 2 downTo 0) {
                // Skips any action of current cell is empty (a zero)
                if (grid[row][col].value == 0) continue

                // current and last cells are identical
                if (grid[row][col].value == lastCellValue) {
                    lastCellValue *= 2
                    grid[lastCellPosition][col].value = lastCellValue
                    grid[row][col].value = 0

                    playerScore.value += lastCellValue
                    if(lastCellValue == 2048) playerScored2048.value = true

                    lastCellValue = 0
                    lastCellPosition--
                    cellsAmount.value--

                    // Positions closest non-zero cells to left
                } else {
                    if (lastCellPosition == 0) break
                    lastCellValue = grid[row][col].value
                    grid[row][col].value = 0
                    if (grid[lastCellPosition][col].value == 0)
                        grid[lastCellPosition][col].value = lastCellValue
                    else
                        grid[--lastCellPosition][col].value = lastCellValue
                }
            }

        }
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                if (gridCopy[row][col] != grid[row][col].value) cantMoveDown.value = false
            }
        }

        if (!cantMoveDown.value) {
            addCell()
            cantMoveLeft.value = false
            cantMoveRight.value = false
            cantMoveUp.value = false
        }
        checkGameOver()
    }

    private fun checkGameOver() {
        gameOver.value = cellsAmount.value == 16 && cantMoveLeft.value && cantMoveRight.value && cantMoveUp.value && cantMoveDown.value || playerScored2048.value
    }
}