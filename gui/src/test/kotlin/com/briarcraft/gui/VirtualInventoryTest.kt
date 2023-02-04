//package com.briarcraft.gui
//
//import org.bukkit.Material
//import org.bukkit.inventory.ItemStack
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.*
//import java.util.NoSuchElementException
//import kotlin.test.*
//
//class VirtualInventoryTest {
//    @Test
//    fun `construct sized virtual inventory`() {
//        assertFailsWith<NegativeArraySizeException> { VirtualInventory(-1) }
//
//        assertNotNull(VirtualInventory(10))
//
//        assertEquals(10, VirtualInventory(10).size)
//    }
//
//    @Nested
//    inner class IteratorTest {
//        @Test
//        fun `iterate empty inventory`() {
//            // Arrange
//            val inv = VirtualInventory(2)
//
//            // Act
//            val iterator = inv.iterator()
//
//            // Assert
//            assertTrue(iterator.hasNext())
//            assertNull(iterator.next())
//            assertTrue(iterator.hasNext())
//            assertNull(iterator.next())
//            assertFalse(iterator.hasNext())
//            assertFailsWith<NoSuchElementException> {
//                assertNull(iterator.next())
//            }
//        }
//
//        @Test
//        fun `iterate populate inventory`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val inv = VirtualInventory(2)
//            inv.setItem(0, itemStack1)
//            inv.setItem(1, itemStack2)
//            assertFailsWith<ArrayIndexOutOfBoundsException> {
//                inv.setItem(2, itemStack1)
//            }
//
//            // Act
//            val iterator = inv.iterator()
//
//            // Assert
//            assertTrue(iterator.hasNext())
//            assertEquals(itemStack1, iterator.next())
//            assertTrue(iterator.hasNext())
//            assertEquals(itemStack2, iterator.next())
//            assertFalse(iterator.hasNext())
//            assertFailsWith<NoSuchElementException> {
//                assertNull(iterator.next())
//            }
//        }
//
//        @Test
//        fun `iterate from index`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val inv = VirtualInventory(2)
//            inv.setItem(0, itemStack1)
//            inv.setItem(1, itemStack2)
//            assertFailsWith<ArrayIndexOutOfBoundsException> {
//                inv.setItem(2, itemStack1)
//            }
//
//            // Act
//            val iterator = inv.iterator(1)
//
//            // Assert
//            assertTrue(iterator.hasNext())
//            assertEquals(itemStack2, iterator.next())
//            assertFalse(iterator.hasNext())
//            assertFailsWith<NoSuchElementException> {
//                assertNull(iterator.next())
//            }
//        }
//
//        @Test
//        fun `iterate before index`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val inv = VirtualInventory(2)
//            inv.setItem(0, itemStack1)
//            inv.setItem(1, itemStack2)
//            assertFailsWith<ArrayIndexOutOfBoundsException> {
//                inv.setItem(2, itemStack1)
//            }
//
//            // Act
//            val iterator = inv.iterator(-2)
//
//            // Assert
//            assertTrue(iterator.hasPrevious())
//            assertEquals(itemStack1, iterator.previous())
//            assertFalse(iterator.hasPrevious())
//            assertFailsWith<NoSuchElementException> {
//                assertNull(iterator.previous())
//            }
//        }
//    }
//
//    @Nested
//    inner class MaxStackSizeTest {
//        @Test
//        fun `get max item stack size`() {
//            val inv = VirtualInventory(10)
//
//            assertEquals(64, inv.maxStackSize)
//        }
//
//        @Test
//        fun `set max item stack size`() {
//            val inv = VirtualInventory(10)
//            inv.maxStackSize = 100
//
//            assertEquals(100, inv.maxStackSize)
//        }
//    }
//
//    @Nested
//    inner class ManageItemsTest {
//        @Test
//        fun `add item to inventory`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {}
//            val inv = VirtualInventory(10)
//            assertTrue(inv.isEmpty)
//
//            // Act
//            val response = inv.addItem(itemStack)
//
//            // Assert
//            assertTrue(response.isEmpty())
//            assertFalse(inv.isEmpty)
//            assertEquals(itemStack, inv.getItem(0))
//        }
//
//        @Test
//        fun `add item to full inventory`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val inv = VirtualInventory(1)
//            inv.addItem(itemStack1)
//
//            // Act
//            val response = inv.addItem(itemStack2)
//
//            // Assert
//            assertEquals(1, response.size)
//            assertEquals(itemStack2, response[0])
//        }
//
//        @Test
//        fun `add multiple items to inventory`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val inv = VirtualInventory(2)
//
//            // Act
//            val response = inv.addItem(itemStack1, itemStack2)
//
//            // Assert
//            assertTrue(response.isEmpty())
//            assertFalse(inv.isEmpty)
//            assertEquals(itemStack1, inv.getItem(0))
//            assertEquals(itemStack2, inv.getItem(1))
//        }
//
//        @Test
//        fun `add multiple items to full inventory`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val itemStack3 = mock<ItemStack> {}
//            val inv = VirtualInventory(1)
//            inv.addItem(itemStack1)
//
//            // Act
//            val response = inv.addItem(itemStack2, itemStack3)
//
//            // Assert
//            assertEquals(2, response.size)
//            assertEquals(itemStack2, response[0])
//            assertEquals(itemStack3, response[1])
//        }
//
//        @Test
//        fun `set item to inventory`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {}
//            val inv = VirtualInventory(10)
//            assertTrue(inv.isEmpty)
//
//            // Act
//            inv.setItem(1, itemStack)
//
//            // Assert
//            assertFalse(inv.isEmpty)
//            assertNull(inv.getItem(0))
//            assertEquals(itemStack, inv.getItem(1))
//        }
//
//        @Test
//        fun `remove item from empty inventory`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { it.isSimilar(any()) } doReturn(false)
//            }
//            val inv = VirtualInventory(10)
//
//            // Act
//            val response = inv.removeItem(itemStack)
//
//            // Assert
//            assertEquals(1, response.size)
//            assertEquals(itemStack, response[0])
//            verify(itemStack, never()).isSimilar(any())
//        }
//
//        @Test
//        fun `remove item from inventory`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { it.isSimilar(any()) } doReturn(true)
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//
//            // Act
//            val response = inv.removeItem(itemStack)
//
//            // Assert
//            assertTrue(response.isEmpty())
//            assertTrue(inv.isEmpty)
//            verify(itemStack).isSimilar(any())
//        }
//
//        @Test
//        fun `remove items from inventory`() {
//            // Arrange
//            val itemStack1 = mock<ItemStack> {
//                on { it.isSimilar(any()) } doReturn(true)
//            }
//            val itemStack2 = mock<ItemStack> {
//                on { it.isSimilar(any()) } doReturn(false)
//            }
//            val itemStack3 = mock<ItemStack> {}
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack1, itemStack3)
//
//            // Act
//            val response = inv.removeItem(itemStack1, itemStack2)
//
//            // Assert
//            assertEquals(1, response.size)
//            assertEquals(itemStack2, response[1])
//            assertFalse(inv.isEmpty)
//            assertEquals(itemStack3, inv.getItem(1))
//            verify(itemStack1).isSimilar(any())
//            verify(itemStack2).isSimilar(any())
//        }
//    }
//
//    @Nested
//    inner class ContentsTest {
//        @Test
//        fun `get contents from empty inventory`() {
//            // Arrange
//            val inv = VirtualInventory(2)
//
//            // Act
//            val response = inv.contents
//
//            // Assert
//            assertEquals(2, response.size)
//            assertNull(response[0])
//            assertNull(response[1])
//        }
//
//        @Test
//        fun `get contents from inventory`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {}
//            val inv = VirtualInventory(2)
//            inv.addItem(itemStack)
//
//            // Act
//            val response = inv.contents
//
//            // Assert
//            assertEquals(2, response.size)
//            assertEquals(itemStack, response[0])
//            assertNull(response[1])
//        }
//
//        @Test
//        fun `set contents`() {
//            // Arrange
//            val inv = VirtualInventory(2)
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val newContents = arrayOf(itemStack1, itemStack2)
//
//            // Act
//            inv.setContents(newContents)
//
//            // Assert
//            assertEquals(itemStack1, inv.getItem(0))
//            assertEquals(itemStack2, inv.getItem(1))
//        }
//
//        @Test
//        fun `set contents that overrides existing contents`() {
//            // Arrange
//            val inv = VirtualInventory(3)
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val itemStack3 = mock<ItemStack> {}
//            val itemStack4 = mock<ItemStack> {}
//            val itemStack5 = mock<ItemStack> {}
//            inv.addItem(itemStack1, itemStack2, itemStack3)
//            val newContents = arrayOf(itemStack4, itemStack5)
//
//            // Act
//            inv.setContents(newContents)
//
//            // Assert
//            assertEquals(itemStack4, inv.getItem(0))
//            assertEquals(itemStack5, inv.getItem(1))
//            assertNull(inv.getItem(2))
//        }
//
//        @Test
//        fun `set contents with too many`() {
//            // Arrange
//            val inv = VirtualInventory(2)
//            val itemStack1 = mock<ItemStack> {}
//            val itemStack2 = mock<ItemStack> {}
//            val itemStack3 = mock<ItemStack> {}
//            val newContents = arrayOf(itemStack1, itemStack2, itemStack3)
//
//            // Act
//            assertFailsWith<IllegalArgumentException> {
//                inv.setContents(newContents)
//            }
//        }
//    }
//
//    @Nested
//    inner class ContainsTest {
//        @Test
//        fun `contains material`() {
//            // Arrange
//            val inv = VirtualInventory(10)
//            val itemStack = mock<ItemStack> {
//                on { type } doReturn(Material.DIRT)
//            }
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.contains(Material.DIRT))
//            assertFalse(inv.contains(Material.DIAMOND))
//        }
//
//        @Test
//        fun `contains itemStack`() {
//            // Arrange
//            val inv = VirtualInventory(10)
//            val itemStack = mock<ItemStack> {}
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.contains(itemStack))
//        }
//
//        @Test
//        fun `contains amount of material`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { type } doReturn Material.DIRT
//                on { amount } doReturn 4
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.contains(Material.DIRT, 4))
//        }
//
//        @Test
//        fun `contains not enough amount of material`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { type } doReturn Material.DIRT
//                on { amount } doReturn 4
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//
//            // Act
//            assertFalse(inv.contains(Material.DIRT, 5))
//        }
//
//        @Test
//        fun `contains more than enough amount of material`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { type } doReturn Material.DIRT
//                on { amount } doReturn 5
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.contains(Material.DIRT, 4))
//        }
//
//        @Test
//        fun `contains count of itemStacks`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { amount } doReturn 10
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//            inv.addItem(itemStack)
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.contains(itemStack, 3))
//        }
//
//        @Test
//        fun `contains at least amount`() {
//            // Arrange
//            val itemStack = mock<ItemStack> {
//                on { isSimilar(any()) } doReturn true
//                on { amount } doReturn 10
//            }
//            val inv = VirtualInventory(10)
//            inv.addItem(itemStack)
//            inv.addItem(itemStack)
//            inv.addItem(itemStack)
//
//            // Act
//            assertTrue(inv.containsAtLeast(itemStack, 0))
//            assertTrue(inv.containsAtLeast(itemStack, 1))
//            assertTrue(inv.containsAtLeast(itemStack, 30))
//            assertFalse(inv.containsAtLeast(itemStack, 31))
//        }
//    }
//}
