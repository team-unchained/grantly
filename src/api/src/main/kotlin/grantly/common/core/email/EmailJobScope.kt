package grantly.common.core.email

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Component

@Component
class EmailJobScope : CoroutineScope by CoroutineScope(Dispatchers.IO)
