package ar.edu.utn.frba.inventario.screens.scan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.OrderProductsViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductsScreen(
    navController: NavController,
    orderId: String?,
    viewModel: OrderProductsViewModel = hiltViewModel()
) {
    val orderProducts by viewModel.orderProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFinishingOrder by viewModel.isFinishingOrder.collectAsState()
    val finishOrderError by viewModel.finishOrderError.collectAsState()
    val orderFinishedSuccessfully by viewModel.orderFinishedSuccessfully.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val screenCoroutineScope = rememberCoroutineScope()
    var currentlyEditingProductId by remember { mutableStateOf<String?>(null) }

    val productConfirmationStatus = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(orderProducts) {
        orderProducts.forEach { product ->
            productConfirmationStatus.putIfAbsent(product.id, false)
        }
    }

    val allProductsConfirmed =
        orderProducts.isNotEmpty() && productConfirmationStatus.values.all { it }

    LaunchedEffect(orderFinishedSuccessfully) {
        when (orderFinishedSuccessfully) {
            true -> {
                snackbarHostState.showSnackbar(
                    message = "¡Pedido finalizado con éxito!",
                    withDismissAction = true
                )
                navController.navigate(Screen.Orders.route) {
                    popUpTo(Screen.Orders.route) { inclusive = true }
                }
            }

            false -> {} //El error ya se muestra en el FloatingActionButton si existe finishOrderError
            null -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        topBar = { OrderTopBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!isLoading && errorMessage == null && orderProducts.isNotEmpty() && !isFinishingOrder) {
                ConfirmOrderButton(
                    allProductsConfirmed = allProductsConfirmed,
                    screenCoroutineScope = screenCoroutineScope,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
                    finishOrderError = finishOrderError
                )
            }
            if (isFinishingOrder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    FloatingActionButton(
                        onClick = {},
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> LoadingView()
                errorMessage != null -> {
                    OrderMessageView(
                        title = stringResource(R.string.search_failed),
                        message = errorMessage ?: stringResource(R.string.order_not_found),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = { navController.popBackStack() },
                        isError = true
                    )
                }

                orderProducts.isEmpty() -> {
                    OrderMessageView(
                        title = stringResource(R.string.order_empty_products),
                        message = stringResource(R.string.no_products_found_for_order),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = { navController.popBackStack() },
                        isError = false
                    )
                }

                else -> {
                    OrderHeader(orderId)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(orderProducts) { product ->
                            ProductListItem(
                                product = product,
                                orderId = orderId.orEmpty(),
                                onQuantityChanged = { productId, newQuantity ->
                                    viewModel.updateProductQuantity(productId, newQuantity)
                                    productConfirmationStatus[productId] = true
                                    currentlyEditingProductId = null
                                },
                                onClick = {
                                    navController.navigate(Screen.ProductDetail.route + "/${product.id}")
                                },
                                onProductConfirmed = { productId ->
                                    productConfirmationStatus[productId] = true
                                    currentlyEditingProductId = null
                                },
                                snackbarHostState = snackbarHostState,
                                isAnotherProductBeingEdited = currentlyEditingProductId != null && currentlyEditingProductId != product.id,
                                onStartEditing = {
                                    currentlyEditingProductId = it
                                    if (productConfirmationStatus[it] == true) {
                                        productConfirmationStatus[it] = false
                                    }
                                },
                                isConfirmed = productConfirmationStatus[product.id] == true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTopBar(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.navigate(Screen.Orders.route) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.order_products_screen_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun ConfirmOrderButton(
    allProductsConfirmed: Boolean,
    screenCoroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: OrderProductsViewModel,
    finishOrderError: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = {
                if (allProductsConfirmed) {
                    viewModel.finishOrder()
                } else {
                    screenCoroutineScope.launch {
                        snackbarHostState.showSnackbar("Confirmar todos los productos antes de finalizar el pedido.")
                    }
                }
            },
            containerColor = if (allProductsConfirmed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
            contentColor = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = stringResource(R.string.confirm_order),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (finishOrderError != null) {
            Text(
                text = finishOrderError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = 50.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun OrderHeader(orderId: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.order_detail_screen_order, orderId.orEmpty()),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.loading_products))
    }
}

@Composable
fun OrderMessageView(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    isError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleColor = if (isError) Color.Red else MaterialTheme.colorScheme.onSurface
        val messageColor =
            if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            fontSize = 18.sp,
            color = messageColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Button(onClick = onButtonClick) {
            Text(buttonText)
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    orderId: String,
    onQuantityChanged: (productId: String, newQuantity: Int) -> Unit,
    onClick: (Product) -> Unit,
    onProductConfirmed: (productId: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    isAnotherProductBeingEdited: Boolean,
    onStartEditing: (productId: String) -> Unit,
    isConfirmed: Boolean
) {
    val context = LocalContext.current
    val expectedQuantity = remember { product.currentStock ?: 0 }
    var editableQuantity by remember(product.currentStock) {
        mutableStateOf(TextFieldValue(product.currentStock?.toString() ?: "0"))
    }
    var isEditingQuantity by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isAnotherProductBeingEdited) {
        if (isAnotherProductBeingEdited && isEditingQuantity) {
            isEditingQuantity = false
            keyboardController?.hide()
            editableQuantity = TextFieldValue(product.currentStock?.toString() ?: "0")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .clickable(enabled = !isEditingQuantity) {
                onClick(product)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(product.imageURL)
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop
                )

                if (product.imageURL?.isNotBlank() == true) {
                    Image(
                        painter = imagePainter,
                        contentDescription = stringResource(R.string.product_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.image_not_found),
                        contentDescription = stringResource(R.string.product_image),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "EAN-13: ${product.ean13}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.expected_quantity, expectedQuantity),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isEditingQuantity) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.received_quantity, ""),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        BasicTextField(
                            value = editableQuantity,
                            onValueChange = { newValue ->
                                if (newValue.text.all { it.isDigit() }) {
                                    editableQuantity = newValue
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Solo se permiten números")
                                    }
                                }
                            },
                            textStyle = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newQuantity = editableQuantity.text.toIntOrNull()
                                    if (newQuantity != null && newQuantity >= 0) {
                                        onQuantityChanged(product.id, newQuantity)
                                        onProductConfirmed(product.id)
                                        isEditingQuantity = false
                                        keyboardController?.hide()
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("La cantidad no puede ser negativa o vacía.")
                                        }
                                    }
                                }
                            ),
                            modifier = Modifier
                                .width(60.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .focusRequester(focusRequester),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }
                    LaunchedEffect(isEditingQuantity) {
                        if (isEditingQuantity) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                            editableQuantity = editableQuantity.copy(
                                selection = TextRange(0, editableQuantity.text.length)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.received_quantity, editableQuantity.text),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
            ) {
                if (isEditingQuantity) {
                    IconButton(onClick = {
                        val newQuantity = editableQuantity.text.toIntOrNull()
                        if (newQuantity != null && newQuantity >= 0) {
                            onQuantityChanged(product.id, newQuantity)
                            onProductConfirmed(product.id)
                            isEditingQuantity = false
                            keyboardController?.hide()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("La cantidad no puede ser negativa o vacía.")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmar cantidad",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (!isAnotherProductBeingEdited) {
                                isEditingQuantity = true
                                onStartEditing(product.id)
                                editableQuantity =
                                    TextFieldValue(product.currentStock?.toString() ?: "0")
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Confirmar la cantidad actual antes de editar otro producto.")
                                }
                            }
                        },
                        enabled = !isAnotherProductBeingEdited
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Cantidad",
                            tint = if (isAnotherProductBeingEdited) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.5f
                            ) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        onProductConfirmed(product.id)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmar producto",
                            tint = if (isConfirmed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}