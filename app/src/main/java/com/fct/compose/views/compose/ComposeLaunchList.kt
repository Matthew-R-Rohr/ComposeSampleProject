package com.fct.compose.views.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fct.compose.R
import com.fct.compose.data.*
import com.fct.compose.extensions.toDateFromUnix
import com.fct.compose.extensions.toMonthDayYearString
import com.fct.compose.viewmodels.DataViewModel
import com.google.android.material.composethemeadapter.MdcTheme

private const val TAG = "composeLaunchList"

@Composable
fun LaunchListScreen(
    viewModel: DataViewModel,
    clickCallback: (entity: LaunchEntity) -> Unit
) {

    val uiState by viewModel.launchUIState.observeAsState()

    Column {
        LaunchFilterSelection {
            // filter selection - trigger data calls
            viewModel.filterSelection = it

            when (it.convertToLaunchType()) {
                LaunchType.LATEST -> viewModel.fetchLatestLaunch()
                LaunchType.UPCOMING -> viewModel.fetchUpcomingLaunches()
                LaunchType.PAST -> viewModel.fetchPastLaunches()
            }
        }

        // content depends on current data state
        when (uiState?.status ?: UIState.Status.INIT) {
            UIState.Status.SUCCESS -> LaunchList(
                clickCallback = clickCallback,
                entityList = uiState?.data ?: emptyList()
            )
            UIState.Status.ERROR -> MessageUI(
                stringResource(
                    R.string.generic_load_error,
                    uiState?.error?.message ?: stringResource(R.string.not_found)
                )
            )
            UIState.Status.LOADING -> LoadingUI()
            UIState.Status.EMPTY -> MessageUI(stringResource(R.string.generic_empty_message))
            UIState.Status.INIT -> viewModel.fetchPastLaunches()
        }
    }
}

@Composable
fun LaunchFilterSelection(
    userSelected: (selectedString: String) -> Unit
) {

    val selectionTypeList = stringArrayResource(R.array.launch_array).asList()
    var text by remember { mutableStateOf(LaunchType.PAST.type) } // initial value
    var isOpen by remember { mutableStateOf(false) } // initial value

    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen = it
    }
    val userSelectedString: (String) -> Unit = {
        text = it
        userSelected(it)
    }

    Box {
        Column {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(text = stringResource(R.string.select_launch_type)) },
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 14.dp)
            )
            DropDownList(
                requestToOpen = isOpen,
                list = selectionTypeList,
                openCloseOfDropDownList,
                userSelectedString
            )
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(start = 12.dp, end = 12.dp, top = 14.dp)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}

@Composable
fun MessageUI(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.spacing_large))
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentHeight()
    )
}

@Composable
fun LoadingUI() {
    LinearProgressIndicator(
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.spacing_large))
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentHeight()
    )
}

@Composable
fun LaunchList(
    entityList: List<LaunchEntity> = List(200) { getComposePreviewLaunchEntity("Launch $it") },
    clickCallback: (entity: LaunchEntity) -> Unit
) {
    // no more need for adapters or RV's or ViewHolders
    LazyColumn(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_small))) {
        items(items = entityList) { entity ->
            Card(
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(R.dimen.spacing_small),
                        horizontal = dimensionResource(R.dimen.spacing_medium)
                    )
                    .clickable { clickCallback(entity) },
                elevation = 2.dp
            ) {
                LaunchItem(entity)
            }
        }
    }
}

@Composable
fun LaunchItem(
    entity: LaunchEntity
) {
    Row(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.spacing_small))
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {

        // badge
        AsyncImage(
            model = entity.missionPatchSmall,
            placeholder = painterResource(R.mipmap.placeholder),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        )

        Column {

            // name
            Text(
                text = entity.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(start = 9.dp).fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(start = 6.dp, top = 5.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                // image count ph
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = R.drawable.ic_baseline_photo_24,
                        placeholder = painterResource(R.drawable.ic_baseline_photo_24),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                    )
                    Text(
                        text = (entity.flickrImages?.size ?: 0).toString(),
                        style = MaterialTheme.typography.body1,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 3.dp)
                    )
                }

                // success
                Text(
                    text = entity.generateSuccessMessage(LocalContext.current),
                    style = MaterialTheme.typography.body1,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small))
                )

                // date
                Text(
                    text = entity.dateUnix.toDateFromUnix().toMonthDayYearString(),
                    style = MaterialTheme.typography.body1,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small))
                )
            }

        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 120)
@Composable
fun MessageLoading() {
    MdcTheme {
        LoadingUI()
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 120)
@Composable
fun MessagePreview() {
    MdcTheme {
        MessageUI("There was an error loading this content\n\n404 Not Found")
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 720)
@Composable
fun ListPreview() {
    MdcTheme {
        Column {
            LaunchFilterSelection {}
            LaunchList {}
        }
    }
}