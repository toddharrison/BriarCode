package com.briarcraft.gui.impl

import com.briarcraft.gui.api.ClickViewHandler
import com.briarcraft.gui.api.UpdateViewHandler
import com.briarcraft.gui.api.UserInterfaceViewHandler
import com.briarcraft.gui.api.ViewHandler

class UserInterfaceViewHandlerImpl(
    override val onCreate: ViewHandler = {},
    override val onOpen: ViewHandler = {},
    override val onUpdate: UpdateViewHandler = { _, _ -> },
    override val onClickOutside: ClickViewHandler = { _, _ -> null },
    override val onClickTop: ClickViewHandler = { _, _ -> null },
    override val onClickBottom: ClickViewHandler = { _, _ -> null },
    override val onClickQuickBar: ClickViewHandler = { _, _ -> null },
    override val onClose: ViewHandler = {}
): UserInterfaceViewHandler
