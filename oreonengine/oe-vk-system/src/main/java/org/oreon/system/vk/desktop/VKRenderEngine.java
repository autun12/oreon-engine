package org.oreon.system.vk.desktop;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkCreateDebugReportCallbackEXT;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_TRUE;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL;
import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_NONE;
import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_VIEWPORT;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_SCISSOR;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_STENCIL_OP_KEEP;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_R;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_G;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_B;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_A;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_CONTENTS_INLINE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceMemoryProperties;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;
import static org.lwjgl.vulkan.VK10.vkCreateCommandPool;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkCreateRenderPass;
import static org.lwjgl.vulkan.VK10.vkCreateBuffer;
import static org.lwjgl.vulkan.VK10.vkGetBufferMemoryRequirements;
import static org.lwjgl.vulkan.VK10.vkAllocateMemory;
import static org.lwjgl.vulkan.VK10.vkMapMemory;
import static org.lwjgl.vulkan.VK10.vkUnmapMemory;
import static org.lwjgl.vulkan.VK10.vkBindBufferMemory;
import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineLayout;
import static org.lwjgl.vulkan.VK10.vkCreateGraphicsPipelines;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;
import static org.lwjgl.vulkan.VK10.vkBeginCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkCreateImageView;
import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
import static org.lwjgl.vulkan.VK10.vkEndCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;
import static org.lwjgl.vulkan.VK10.vkDestroyFramebuffer;
import static org.lwjgl.vulkan.VK10.vkCreateFramebuffer;
import static org.lwjgl.vulkan.VK10.vkResetCommandPool;
import static org.lwjgl.vulkan.VK10.vkCmdBeginRenderPass;
import static org.lwjgl.vulkan.VK10.vkCmdSetViewport;
import static org.lwjgl.vulkan.VK10.vkCmdSetScissor;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdBindVertexBuffers;
import static org.lwjgl.vulkan.VK10.vkCmdDraw;
import static org.lwjgl.vulkan.VK10.vkCmdEndRenderPass;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceExtensionProperties;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceFeatures;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSubpassDescription;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkLayerProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.ResourceLoader;
import org.oreon.core.vk.util.VKUtil;

public class VKRenderEngine implements RenderEngine{
	
	private VkInstance vkInstance;
	private VkPhysicalDevice physicalDevice;
	
	private VkDevice device;
	private VkQueue queue;
	private VkSubmitInfo submitInfo;
	private DeviceAndGraphicsQueueFamily deviceAndGraphicsQueueFamily;
	private IntBuffer pImageIndex;
	private PointerBuffer pCommandBuffers;
	private SwapchainRecreator swapchainRecreator;
	private LongBuffer pSwapchains;
	
	private VkPresentInfoKHR presentInfo;
	
	private int currentBuffer;
	private final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;
	
	private VkDebugReportCallbackEXT debugCallback;
	private long debugCallbackHandle;
	
	private VkSemaphoreCreateInfo semaphoreCreateInfo;
	private LongBuffer pImageAcquiredSemaphore;
	private LongBuffer pRenderCompleteSemaphore;
	
	/*
     * All resources that must be reallocated on window resize.
     */
    private static Swapchain swapchain;
    private static long[] framebuffers;
    private static int width, height;
    private static VkCommandBuffer[] renderCommandBuffers;
	
	private ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	 
	private final boolean validation = Boolean.parseBoolean(System.getProperty("vulkan.validation", "true"));
	
	private class DeviceAndGraphicsQueueFamily {
        VkDevice device;
        int queueFamilyIndex;
        VkPhysicalDeviceMemoryProperties memoryProperties;
    }
	
	private class ColorFormatAndSpace {
        int colorFormat;
        int colorSpace;
	}
	
	private class Vertices {
        long verticesBuf;
        VkPipelineVertexInputStateCreateInfo createInfo;
    }
	
	private class Swapchain {
        long swapchainHandle;
        long[] images;
        long[] imageViews;
    }
	
	class SwapchainRecreator {
        boolean mustRecreate = true;
        void recreate(VkCommandBuffer setupCommandBuffer,
        			  VkDevice device,
        			  VkPhysicalDevice physicalDevice,
        			  long surface,
        			  ColorFormatAndSpace colorFormatAndSpace,
        			  VkQueue queue,
        			  long renderPass,
        			  long renderCommandPool,
        			  long pipeline,
        			  Vertices vertices) {
            // Begin the setup command buffer (the one we will use for swapchain/framebuffer creation)
            VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                    .pNext(0);
            int err = vkBeginCommandBuffer(setupCommandBuffer, cmdBufInfo);
            cmdBufInfo.free();
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to begin setup command buffer: " + VKUtil.translateVulkanResult(err));
            }
            long oldChain = swapchain != null ? swapchain.swapchainHandle : VK_NULL_HANDLE;
            // Create the swapchain (this will also add a memory barrier to initialize the framebuffer images)
            swapchain = createSwapChain(device, physicalDevice, surface, oldChain, setupCommandBuffer,
                    width, height, colorFormatAndSpace.colorFormat, colorFormatAndSpace.colorSpace);
            err = vkEndCommandBuffer(setupCommandBuffer);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to end setup command buffer: " + VKUtil.translateVulkanResult(err));
            }
            submitCommandBuffer(queue, setupCommandBuffer);
            vkQueueWaitIdle(queue);

            if (framebuffers != null) {
                for (int i = 0; i < framebuffers.length; i++)
                    vkDestroyFramebuffer(device, framebuffers[i], null);
            }
            framebuffers = createFramebuffers(device, swapchain, renderPass, width, height);
            // Create render command buffers
            if (renderCommandBuffers != null) {
                vkResetCommandPool(device, renderCommandPool, 0);
            }
            renderCommandBuffers = createRenderCommandBuffers(device, renderCommandPool, framebuffers, renderPass, width, height, pipeline,
                    vertices.verticesBuf);

            mustRecreate = false;
        }
    }
	
	@Override
	public void init() {
		
		if (!glfwVulkanSupported()) {
	            throw new AssertionError("GLFW failed to find the Vulkan loader");
	        }
		
		PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new AssertionError("Failed to find list of required Vulkan extensions");
        }
        
        vkInstance = createVkInstance(requiredExtensions); 
        
        VkDebugReportCallbackEXT debugCallback = new VkDebugReportCallbackEXT() {
            public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                System.err.println("ERROR OCCURED: " + VkDebugReportCallbackEXT.getString(pMessage));
                return 0;
            }
        };
        debugCallbackHandle = setupDebugging(vkInstance, VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT, debugCallback);
        
        physicalDevice = getFirstPhysicalDevice(vkInstance);
        
        getPhysicalDeviceProperties(physicalDevice);
        
        getPhysicalDeviceFeatures(physicalDevice);
        
        deviceAndGraphicsQueueFamily = createDeviceAndGetGraphicsQueueFamily(physicalDevice);
        
        device = deviceAndGraphicsQueueFamily.device;
        int queueFamilyIndex = deviceAndGraphicsQueueFamily.queueFamilyIndex;
        VkPhysicalDeviceMemoryProperties memoryProperties = deviceAndGraphicsQueueFamily.memoryProperties;
	
		LongBuffer pSurface = memAllocLong(1);
	    int err = glfwCreateWindowSurface(vkInstance, CoreSystem.getInstance().getWindow().getId(), null, pSurface);
	    
	    long surface = pSurface.get(0);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create surface: " + VKUtil.translateVulkanResult(err));
	    }
	    
	    ColorFormatAndSpace colorFormatAndSpace = getColorFormatAndSpace(physicalDevice, surface);
	    long commandPool = createCommandPool(device, queueFamilyIndex);
	    VkCommandBuffer setupCommandBuffer = createCommandBuffer(device, commandPool);
	    VkCommandBuffer postPresentCommandBuffer = createCommandBuffer(device, commandPool);
	    queue = createDeviceQueue(device, queueFamilyIndex);
	    long renderPass = createRenderPass(device, colorFormatAndSpace.colorFormat);
	    long renderCommandPool = createCommandPool(device, queueFamilyIndex);
	    Vertices vertices = createVertices(memoryProperties, device);
	    long pipeline = createPipeline(device, renderPass, vertices.createInfo);
	    
	    swapchainRecreator = new SwapchainRecreator();
	    
	    pImageIndex = memAllocInt(1);
        currentBuffer = 0;
        pCommandBuffers = memAllocPointer(1);
        pSwapchains = memAllocLong(1);
        pImageAcquiredSemaphore = memAllocLong(1);
        pRenderCompleteSemaphore = memAllocLong(1);
        
        semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(0)
                .flags(0);
        
        IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
        
        submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pNext(0)
                .waitSemaphoreCount(pImageAcquiredSemaphore.remaining())
                .pWaitSemaphores(pImageAcquiredSemaphore)
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(pCommandBuffers)
                .pSignalSemaphores(pRenderCompleteSemaphore);
        
        presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pNext(0)
                .pWaitSemaphores(pRenderCompleteSemaphore)
                .swapchainCount(pSwapchains.remaining())
                .pSwapchains(pSwapchains)
                .pImageIndices(pImageIndex)
                .pResults(null);
        
        if (swapchainRecreator.mustRecreate)
            swapchainRecreator.recreate(setupCommandBuffer,
            							device,
            							physicalDevice,
            							surface,
            							colorFormatAndSpace,
            							queue,
            							renderPass,
            		        			renderCommandPool,
            		        			pipeline,
            		        			vertices);
	}
    

	@Override
	public void render() {
		
//		// Create a semaphore to wait for the swapchain to acquire the next image
//        int err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);
//        if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to create image acquired semaphore: " + VKUtil.translateVulkanResult(err));
//        }
//        
//        // Create a semaphore to wait for the render to complete, before presenting
//        err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderCompleteSemaphore);
//        if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to create render complete semaphore: " + VKUtil.translateVulkanResult(err));
//        }
//        
//        // Get next image from the swap chain (back/front buffer).
//        // This will setup the imageAquiredSemaphore to be signalled when the operation is complete
//        err = vkAcquireNextImageKHR(device, swapchain.swapchainHandle, UINT64_MAX, pImageAcquiredSemaphore.get(0), VK_NULL_HANDLE, pImageIndex);
//        currentBuffer = pImageIndex.get(0);
//        if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to acquire next swapchain image: " + VKUtil.translateVulkanResult(err));
//        }
//        
//        // Select the command buffer for the current framebuffer image/attachment
//        pCommandBuffers.put(0, renderCommandBuffers[currentBuffer]);
//
//        // Submit to the graphics queue
//        err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
//        if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to submit render queue: " + VKUtil.translateVulkanResult(err));
//        }
//
//        // Present the current buffer to the swap chain
//        // This will display the image
//        pSwapchains.put(0, swapchain.swapchainHandle);
//        err = vkQueuePresentKHR(queue, presentInfo);
//        if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to present the swapchain image: " + VKUtil.translateVulkanResult(err));
//        }
//        
//        // Create and submit post present barrier
//        vkQueueWaitIdle(queue);
//
//        // Destroy this semaphore (we will create a new one in the next frame)
////        vkDestroySemaphore(device, pImageAcquiredSemaphore.get(0), null);
////        vkDestroySemaphore(device, pRenderCompleteSemaphore.get(0), null);
////        submitPostPresentBarrier(swapchain.images[currentBuffer], postPresentCommandBuffer, queue);

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	private VkInstance createVkInstance(PointerBuffer requiredExtensions) {
		
        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(memUTF8("GLFW Vulkan Demo"))
                .pEngineName(memUTF8(""))
                .apiVersion(VK_MAKE_VERSION(1, 0, 2));
        
        PointerBuffer ppEnabledExtensionNames = memAllocPointer(requiredExtensions.remaining() + 1);
        ppEnabledExtensionNames.put(requiredExtensions);
        
        ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        
        ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
        ppEnabledExtensionNames.flip();
        
        checkExtensionSupport(ppEnabledExtensionNames);
        
        PointerBuffer ppEnabledLayerNames = memAllocPointer(layers.length);
        for (int i = 0; validation && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();
        
        checkValidationLayerSupport(ppEnabledLayerNames);
        
        VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(0)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(ppEnabledExtensionNames)
                .ppEnabledLayerNames(ppEnabledLayerNames);
        PointerBuffer pInstance = memAllocPointer(1);
        int err = vkCreateInstance(pCreateInfo, null, pInstance);
        long instance = pInstance.get(0);
    
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: " + VKUtil.translateVulkanResult(err));
        }
        VkInstance ret = new VkInstance(instance, pCreateInfo);
        
        pCreateInfo.free();
        memFree(pInstance);
        memFree(ppEnabledLayerNames);
        memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
        memFree(ppEnabledExtensionNames);
        memFree(appInfo.pApplicationName());
        memFree(appInfo.pEngineName());
        appInfo.free();
        
        return ret;
    }
	
	public void checkExtensionSupport(PointerBuffer ppEnabledExtensionNames){
		
		IntBuffer extensionCount = memAllocInt(1);
		
		int err = vkEnumerateInstanceExtensionProperties("", extensionCount, null);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		VkExtensionProperties.Buffer extensions = VkExtensionProperties.calloc(extensionCount.get(0));
				
		err = vkEnumerateInstanceExtensionProperties("", extensionCount, extensions);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		List<String> availableExtensions = new ArrayList<>();
		for (VkExtensionProperties extension : extensions){
			availableExtensions.add(extension.extensionNameString());
		}
		
		for (int i=0; i<ppEnabledExtensionNames.limit(); i++){
			if (!availableExtensions.contains(ppEnabledExtensionNames.getStringUTF8())){
				throw new AssertionError("Extension " + ppEnabledExtensionNames.getStringUTF8() + " not supported");
			}
		}
		
		ppEnabledExtensionNames.flip();
		
		memFree(extensionCount);
		extensions.free();
	}
	
	public void checkValidationLayerSupport(PointerBuffer ppEnabledLayerNames){
		
		IntBuffer layerCount = memAllocInt(1);
		
		int err = vkEnumerateInstanceLayerProperties(layerCount, null);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		VkLayerProperties.Buffer layers = VkLayerProperties.calloc(layerCount.get(0));
		
		err = vkEnumerateInstanceLayerProperties(layerCount, layers);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		List<String> availableLayers = new ArrayList<>();
		for (VkLayerProperties layer : layers){
			availableLayers.add(layer.layerNameString());
		}
		
		for (int i=0; i<ppEnabledLayerNames.limit(); i++){
			if (!availableLayers.contains(ppEnabledLayerNames.getStringUTF8())){
				throw new AssertionError("Extension " + ppEnabledLayerNames.getStringUTF8() + " not supported");
			}
		}

		ppEnabledLayerNames.flip();
		
		memFree(layerCount);
		layers.free();
	}
	
	private VkPhysicalDevice getFirstPhysicalDevice(VkInstance instance) {
		
        IntBuffer pPhysicalDeviceCount = memAllocInt(1);
        int err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical devices: " + VKUtil.translateVulkanResult(err));
        }
        
        System.out.println("Available Physical Devices: " + pPhysicalDeviceCount.get(0));
        
        PointerBuffer pPhysicalDevices = memAllocPointer(pPhysicalDeviceCount.get(0));
        err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
        long physicalDevice = pPhysicalDevices.get(0);
       
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical devices: " + VKUtil.translateVulkanResult(err));
        }
        
        memFree(pPhysicalDeviceCount);
        memFree(pPhysicalDevices);
        
        return new VkPhysicalDevice(physicalDevice, instance);
    }
	
	public void getPhysicalDeviceProperties(VkPhysicalDevice physicalDevice){
		
		VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.create();
		vkGetPhysicalDeviceProperties(physicalDevice, properties);
		System.out.println("Physical Device: " + properties.deviceNameString());
	}
	
	public void getPhysicalDeviceFeatures(VkPhysicalDevice physicalDevice){
		
		VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create();
		vkGetPhysicalDeviceFeatures(physicalDevice, features);
	}
	
	private long setupDebugging(VkInstance instance, int flags, VkDebugReportCallbackEXT callback) {
		
        VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pNext(0)
                .pfnCallback(callback)
                .pUserData(0)
                .flags(flags);
        
        LongBuffer pCallback = memAllocLong(1);
        int err = vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback);
        long callbackHandle = pCallback.get(0);
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: " + VKUtil.translateVulkanResult(err));
        }
        
        memFree(pCallback);
        dbgCreateInfo.free();
        
        return callbackHandle;
    }
	
	private DeviceAndGraphicsQueueFamily createDeviceAndGetGraphicsQueueFamily(VkPhysicalDevice physicalDevice) {
		
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        
        System.out.println("Queue Families: " +  queueCount);
        
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        
        int graphicsQueueFamilyIndex;
        for (graphicsQueueFamilyIndex = 0; graphicsQueueFamilyIndex < queueCount; graphicsQueueFamilyIndex++) {
            if ((queueProps.get(graphicsQueueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                break;
        }
        queueProps.free();
        FloatBuffer pQueuePriorities = memAllocFloat(1).put(0.0f);
        pQueuePriorities.flip();
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        PointerBuffer ppEnabledLayerNames = memAllocPointer(layers.length);
        for (int i = 0; validation && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();

        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(0)
                .pQueueCreateInfos(queueCreateInfo)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        int err = vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice);
        long device = pDevice.get(0);
       
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create device: " + VKUtil.translateVulkanResult(err));
        }

        VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);

        DeviceAndGraphicsQueueFamily ret = new DeviceAndGraphicsQueueFamily();
        ret.device = new VkDevice(device, physicalDevice, deviceCreateInfo);
        ret.queueFamilyIndex = graphicsQueueFamilyIndex;
        ret.memoryProperties = memoryProperties;

        deviceCreateInfo.free();
        memFree(pQueueFamilyPropertyCount);
        memFree(pDevice);
        memFree(ppEnabledLayerNames);
        memFree(VK_KHR_SWAPCHAIN_EXTENSION);
        memFree(extensions);
        memFree(pQueuePriorities);
        return ret;
    }
	
	 private ColorFormatAndSpace getColorFormatAndSpace(VkPhysicalDevice physicalDevice, long surface) {
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        memFree(pQueueFamilyPropertyCount);

        // Iterate over each queue to learn whether it supports presenting:
        IntBuffer supportsPresent = memAllocInt(queueCount);
        for (int i = 0; i < queueCount; i++) {
            supportsPresent.position(i);
            int err = vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to physical device surface support: ");// + translateVulkanResult(err));
            }
        }

        // Search for a graphics and a present queue in the array of queue families, try to find one that supports both
        int graphicsQueueNodeIndex = Integer.MAX_VALUE;
        int presentQueueNodeIndex = Integer.MAX_VALUE;
        for (int i = 0; i < queueCount; i++) {
            if ((queueProps.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                    graphicsQueueNodeIndex = i;
                }
                if (supportsPresent.get(i) == VK_TRUE) {
                    graphicsQueueNodeIndex = i;
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        queueProps.free();
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            // If there's no queue that supports both present and graphics try to find a separate present queue
            for (int i = 0; i < queueCount; ++i) {
                if (supportsPresent.get(i) == VK_TRUE) {
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        memFree(supportsPresent);

        // Generate error if could not find both a graphics and a present queue
        if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No graphics queue found");
        }
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No presentation queue found");
        }
        if (graphicsQueueNodeIndex != presentQueueNodeIndex) {
            throw new AssertionError("Presentation queue != graphics queue");
        }

        // Get list of supported formats
        IntBuffer pFormatCount = memAllocInt(1);
        int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null);
        int formatCount = pFormatCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query number of physical device surface formats: " + VKUtil.translateVulkanResult(err));
        }

        VkSurfaceFormatKHR.Buffer surfFormats = VkSurfaceFormatKHR.calloc(formatCount);
        err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, surfFormats);
        memFree(pFormatCount);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query physical device surface formats: " + VKUtil.translateVulkanResult(err));
        }

        int colorFormat;
        if (formatCount == 1 && surfFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
            colorFormat = VK_FORMAT_B8G8R8A8_UNORM;
        } else {
            colorFormat = surfFormats.get(0).format();
        }
        int colorSpace = surfFormats.get(0).colorSpace();
        surfFormats.free();

        ColorFormatAndSpace ret = new ColorFormatAndSpace();
        ret.colorFormat = colorFormat;
        ret.colorSpace = colorSpace;
        return ret;
    }
	 
	 private long createCommandPool(VkDevice device, int queueNodeIndex) {
        VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .queueFamilyIndex(queueNodeIndex)
                .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
        LongBuffer pCmdPool = memAllocLong(1);
        int err = vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);
        long commandPool = pCmdPool.get(0);
        cmdPoolInfo.free();
        memFree(pCmdPool);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create command pool: " + VKUtil.translateVulkanResult(err));
        }
        return commandPool;
    }
	 
	 private VkCommandBuffer createCommandBuffer(VkDevice device, long commandPool) {
        VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(1);
        PointerBuffer pCommandBuffer = memAllocPointer(1);
        int err = vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
        cmdBufAllocateInfo.free();
        long commandBuffer = pCommandBuffer.get(0);
        memFree(pCommandBuffer);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to allocate command buffer: " + VKUtil.translateVulkanResult(err));
        }
        return new VkCommandBuffer(commandBuffer, device);
    }
	 
	 private VkQueue createDeviceQueue(VkDevice device, int queueFamilyIndex) {
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(device, queueFamilyIndex, 0, pQueue);
        long queue = pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue, device);
    }
	 
	 private long createRenderPass(VkDevice device, int colorFormat) {
        VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(1)
                .format(colorFormat)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        VkAttachmentReference.Buffer colorReference = VkAttachmentReference.calloc(1)
                .attachment(0)
                .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        VkSubpassDescription.Buffer subpass = VkSubpassDescription.calloc(1)
                .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                .flags(0)
                .pInputAttachments(null)
                .colorAttachmentCount(colorReference.remaining())
                .pColorAttachments(colorReference) // <- only color attachment
                .pResolveAttachments(null)
                .pDepthStencilAttachment(null)
                .pPreserveAttachments(null);

        VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                .pNext(0)
                .pAttachments(attachments)
                .pSubpasses(subpass)
                .pDependencies(null);

        LongBuffer pRenderPass = memAllocLong(1);
        int err = vkCreateRenderPass(device, renderPassInfo, null, pRenderPass);
        long renderPass = pRenderPass.get(0);
        memFree(pRenderPass);
        renderPassInfo.free();
        colorReference.free();
        subpass.free();
        attachments.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create clear render pass: " + VKUtil.translateVulkanResult(err));
        }
        
        return renderPass;
    }
	 
	 private Vertices createVertices(VkPhysicalDeviceMemoryProperties deviceMemoryProperties, VkDevice device) {
		 
        ByteBuffer vertexBuffer = memAlloc(3 * 2 * 4);
        FloatBuffer fb = vertexBuffer.asFloatBuffer();
        // The triangle will showup upside-down, because Vulkan does not do proper viewport transformation to
        // account for inverted Y axis between the window coordinate system and clip space/NDC
        fb.put(-0.5f).put(-0.5f);
        fb.put( 0.5f).put(-0.5f);
        fb.put( 0.0f).put( 0.5f);

        VkMemoryAllocateInfo memAlloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .pNext(0)
                .allocationSize(0)
                .memoryTypeIndex(0);
        VkMemoryRequirements memReqs = VkMemoryRequirements.calloc();

        int err;

        // Generate vertex buffer
        //  Setup
        VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .pNext(0)
                .size(vertexBuffer.remaining())
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .flags(0);
        LongBuffer pBuffer = memAllocLong(1);
        err = vkCreateBuffer(device, bufInfo, null, pBuffer);
        long verticesBuf = pBuffer.get(0);
        memFree(pBuffer);
        bufInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create vertex buffer: " + VKUtil.translateVulkanResult(err));
        }

        vkGetBufferMemoryRequirements(device, verticesBuf, memReqs);
        memAlloc.allocationSize(memReqs.size());
        IntBuffer memoryTypeIndex = memAllocInt(1);
        getMemoryType(deviceMemoryProperties, memReqs.memoryTypeBits(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, memoryTypeIndex);
        memAlloc.memoryTypeIndex(memoryTypeIndex.get(0));
        memFree(memoryTypeIndex);
        memReqs.free();

        LongBuffer pMemory = memAllocLong(1);
        err = vkAllocateMemory(device, memAlloc, null, pMemory);
        long verticesMem = pMemory.get(0);
        memFree(pMemory);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to allocate vertex memory: " + VKUtil.translateVulkanResult(err));
        }

        PointerBuffer pData = memAllocPointer(1);
        err = vkMapMemory(device, verticesMem, 0, memAlloc.allocationSize(), 0, pData);
        memAlloc.free();
        long data = pData.get(0);
        memFree(pData);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to map vertex memory: " + VKUtil.translateVulkanResult(err));
        }

        memCopy(memAddress(vertexBuffer), data, vertexBuffer.remaining());
        memFree(vertexBuffer);
        vkUnmapMemory(device, verticesMem);
        err = vkBindBufferMemory(device, verticesBuf, verticesMem, 0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to bind memory to vertex buffer: " + VKUtil.translateVulkanResult(err));
        }

        // Binding description
        VkVertexInputBindingDescription.Buffer bindingDescriptor = VkVertexInputBindingDescription.calloc(1)
                .binding(0) // <- we bind our vertex buffer to point 0
                .stride(2 * 4)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        // Attribute descriptions
        // Describes memory layout and shader attribute locations
        VkVertexInputAttributeDescription.Buffer attributeDescriptions = VkVertexInputAttributeDescription.calloc(1);
        // Location 0 : Position
        attributeDescriptions.get(0)
                .binding(0) // <- binding point used in the VkVertexInputBindingDescription
                .location(0) // <- location in the shader's attribute layout (inside the shader source)
                .format(VK_FORMAT_R32G32_SFLOAT)
                .offset(0);

        // Assign to vertex buffer
        VkPipelineVertexInputStateCreateInfo vi = VkPipelineVertexInputStateCreateInfo.calloc();
        vi.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);
        vi.pNext(0);
        vi.pVertexBindingDescriptions(bindingDescriptor);
        vi.pVertexAttributeDescriptions(attributeDescriptions);

        Vertices ret = new Vertices();
        ret.createInfo = vi;
        ret.verticesBuf = verticesBuf;
        return ret;
    }
	 
	 private long createPipeline(VkDevice device, long renderPass, VkPipelineVertexInputStateCreateInfo vi) {
        int err;
        // Vertex input state
        // Describes the topoloy used with this pipeline
        VkPipelineInputAssemblyStateCreateInfo inputAssemblyState = VkPipelineInputAssemblyStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);

        // Rasterization state
        VkPipelineRasterizationStateCreateInfo rasterizationState = VkPipelineRasterizationStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                .polygonMode(VK_POLYGON_MODE_FILL)
                .cullMode(VK_CULL_MODE_NONE)
                .frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
                .depthClampEnable(false)
                .rasterizerDiscardEnable(false)
                .depthBiasEnable(false);

        // Color blend state
        // Describes blend modes and color masks
        VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                .blendEnable(false)
                .colorWriteMask(0xF); // <- RGBA
        VkPipelineColorBlendStateCreateInfo colorBlendState = VkPipelineColorBlendStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .pAttachments(colorWriteMask);

        // Viewport state
        VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1) // <- one viewport
                .scissorCount(1); // <- one scissor rectangle

        // Enable dynamic states
        // Describes the dynamic states to be used with this pipeline
        // Dynamic states can be set even after the pipeline has been created
        // So there is no need to create new pipelines just for changing
        // a viewport's dimensions or a scissor box
        IntBuffer pDynamicStates = memAllocInt(2);
        pDynamicStates.put(VK_DYNAMIC_STATE_VIEWPORT).put(VK_DYNAMIC_STATE_SCISSOR).flip();
        VkPipelineDynamicStateCreateInfo dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
                // The dynamic state properties themselves are stored in the command buffer
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(pDynamicStates);

        // Depth and stencil state
        // Describes depth and stenctil test and compare ops
        VkPipelineDepthStencilStateCreateInfo depthStencilState = VkPipelineDepthStencilStateCreateInfo.calloc()
                // No depth test/write and no stencil used 
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                .depthTestEnable(false)
                .depthWriteEnable(false)
                .depthCompareOp(VK_COMPARE_OP_ALWAYS)
                .depthBoundsTestEnable(false)
                .stencilTestEnable(false);
        depthStencilState.back()
                .failOp(VK_STENCIL_OP_KEEP)
                .passOp(VK_STENCIL_OP_KEEP)
                .compareOp(VK_COMPARE_OP_ALWAYS);
        depthStencilState.front(depthStencilState.back());

        // Multi sampling state
        // No multi sampling used in this example
        VkPipelineMultisampleStateCreateInfo multisampleState = VkPipelineMultisampleStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .pSampleMask(null)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

        // Load shaders
        VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(2);
        try {
			shaderStages.get(0).set(loadShader(device, "shaders/triangle.vert.spv", VK_SHADER_STAGE_VERTEX_BIT));
			shaderStages.get(1).set(loadShader(device, "shaders/triangle.frag.spv", VK_SHADER_STAGE_FRAGMENT_BIT));
		} catch (IOException e) {
			e.printStackTrace();
		}

        // Create the pipeline layout that is used to generate the rendering pipelines that
        // are based on this descriptor set layout
        VkPipelineLayoutCreateInfo pPipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(0)
                .pSetLayouts(null);

        LongBuffer pPipelineLayout = memAllocLong(1);
        err = vkCreatePipelineLayout(device, pPipelineLayoutCreateInfo, null, pPipelineLayout);
        long layout = pPipelineLayout.get(0);
        memFree(pPipelineLayout);
        pPipelineLayoutCreateInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create pipeline layout: " + VKUtil.translateVulkanResult(err));
        }

        // Assign states
        VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .layout(layout) // <- the layout used for this pipeline (NEEDS TO BE SET! even though it is basically empty)
                .renderPass(renderPass) // <- renderpass this pipeline is attached to
                .pVertexInputState(vi)
                .pInputAssemblyState(inputAssemblyState)
                .pRasterizationState(rasterizationState)
                .pColorBlendState(colorBlendState)
                .pMultisampleState(multisampleState)
                .pViewportState(viewportState)
                .pDepthStencilState(depthStencilState)
                .pStages(shaderStages)
                .pDynamicState(dynamicState);

        // Create rendering pipeline
        LongBuffer pPipelines = memAllocLong(1);
        err = vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines);
        long pipeline = pPipelines.get(0);
        shaderStages.free();
        multisampleState.free();
        depthStencilState.free();
        dynamicState.free();
        memFree(pDynamicStates);
        viewportState.free();
        colorBlendState.free();
        colorWriteMask.free();
        rasterizationState.free();
        inputAssemblyState.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create pipeline: " + VKUtil.translateVulkanResult(err));
        }
        return pipeline;
    }
	 
	 private Swapchain createSwapChain(VkDevice device, VkPhysicalDevice physicalDevice, long surface, long oldSwapChain, VkCommandBuffer commandBuffer, int newWidth,
	            int newHeight, int colorFormat, int colorSpace) {
	        int err;
	        // Get physical device surface properties and formats
	        VkSurfaceCapabilitiesKHR surfCaps = VkSurfaceCapabilitiesKHR.calloc();
	        err = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfCaps);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to get physical device surface capabilities: " + VKUtil.translateVulkanResult(err));
	        }

	        IntBuffer pPresentModeCount = memAllocInt(1);
	        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null);
	        int presentModeCount = pPresentModeCount.get(0);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to get number of physical device surface presentation modes: " + VKUtil.translateVulkanResult(err));
	        }

	        IntBuffer pPresentModes = memAllocInt(presentModeCount);
	        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, pPresentModes);
	        memFree(pPresentModeCount);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to get physical device surface presentation modes: " + VKUtil.translateVulkanResult(err));
	        }

	        // Try to use mailbox mode. Low latency and non-tearing
	        int swapchainPresentMode = VK_PRESENT_MODE_FIFO_KHR;
	        for (int i = 0; i < presentModeCount; i++) {
	            if (pPresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
	                swapchainPresentMode = VK_PRESENT_MODE_MAILBOX_KHR;
	                break;
	            }
	            if ((swapchainPresentMode != VK_PRESENT_MODE_MAILBOX_KHR) && (pPresentModes.get(i) == VK_PRESENT_MODE_IMMEDIATE_KHR)) {
	                swapchainPresentMode = VK_PRESENT_MODE_IMMEDIATE_KHR;
	            }
	        }
	        memFree(pPresentModes);

	        // Determine the number of images
	        int desiredNumberOfSwapchainImages = surfCaps.minImageCount() + 1;
	        if ((surfCaps.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > surfCaps.maxImageCount())) {
	            desiredNumberOfSwapchainImages = surfCaps.maxImageCount();
	        }

	        VkExtent2D currentExtent = surfCaps.currentExtent();
	        int currentWidth = currentExtent.width();
	        int currentHeight = currentExtent.height();
	        if (currentWidth != -1 && currentHeight != -1) {
	            width = currentWidth;
	            height = currentHeight;
	        } else {
	            width = newWidth;
	            height = newHeight;
	        }

	        int preTransform;
	        if ((surfCaps.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
	            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
	        } else {
	            preTransform = surfCaps.currentTransform();
	        }
	        surfCaps.free();

	        VkSwapchainCreateInfoKHR swapchainCI = VkSwapchainCreateInfoKHR.calloc()
	                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
	                .pNext(0)
	                .surface(surface)
	                .minImageCount(desiredNumberOfSwapchainImages)
	                .imageFormat(colorFormat)
	                .imageColorSpace(colorSpace)
	                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
	                .preTransform(preTransform)
	                .imageArrayLayers(1)
	                .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
	                .pQueueFamilyIndices(null)
	                .presentMode(swapchainPresentMode)
	                .oldSwapchain(oldSwapChain)
	                .clipped(true)
	                .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
	        swapchainCI.imageExtent()
	                .width(width)
	                .height(height);
	        LongBuffer pSwapChain = memAllocLong(1);
	        err = vkCreateSwapchainKHR(device, swapchainCI, null, pSwapChain);
	        swapchainCI.free();
	        long swapChain = pSwapChain.get(0);
	        memFree(pSwapChain);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to create swap chain: " + VKUtil.translateVulkanResult(err));
	        }

	        // If we just re-created an existing swapchain, we should destroy the old swapchain at this point.
	        // Note: destroying the swapchain also cleans up all its associated presentable images once the platform is done with them.
	        if (oldSwapChain != VK_NULL_HANDLE) {
	            vkDestroySwapchainKHR(device, oldSwapChain, null);
	        }

	        IntBuffer pImageCount = memAllocInt(1);
	        err = vkGetSwapchainImagesKHR(device, swapChain, pImageCount, null);
	        int imageCount = pImageCount.get(0);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to get number of swapchain images: " + VKUtil.translateVulkanResult(err));
	        }

	        LongBuffer pSwapchainImages = memAllocLong(imageCount);
	        err = vkGetSwapchainImagesKHR(device, swapChain, pImageCount, pSwapchainImages);
	        if (err != VK_SUCCESS) {
	            throw new AssertionError("Failed to get swapchain images: " + VKUtil.translateVulkanResult(err));
	        }
	        memFree(pImageCount);

	        long[] images = new long[imageCount];
	        long[] imageViews = new long[imageCount];
	        LongBuffer pBufferView = memAllocLong(1);
	        VkImageViewCreateInfo colorAttachmentView = VkImageViewCreateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
	                .pNext(0)
	                .format(colorFormat)
	                .viewType(VK_IMAGE_VIEW_TYPE_2D)
	                .flags(0);
	        colorAttachmentView.components()
	                .r(VK_COMPONENT_SWIZZLE_R)
	                .g(VK_COMPONENT_SWIZZLE_G)
	                .b(VK_COMPONENT_SWIZZLE_B)
	                .a(VK_COMPONENT_SWIZZLE_A);
	        colorAttachmentView.subresourceRange()
	                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
	                .baseMipLevel(0)
	                .levelCount(1)
	                .baseArrayLayer(0)
	                .layerCount(1);
	        for (int i = 0; i < imageCount; i++) {
	            images[i] = pSwapchainImages.get(i);
	            // Bring the image from an UNDEFINED state to the VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT state
	            createImageBarrier(commandBuffer, images[i], VK_IMAGE_ASPECT_COLOR_BIT,
	                    VK_IMAGE_LAYOUT_UNDEFINED, 0,
	                    VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);
	            colorAttachmentView.image(images[i]);
	            err = vkCreateImageView(device, colorAttachmentView, null, pBufferView);
	            imageViews[i] = pBufferView.get(0);
	            if (err != VK_SUCCESS) {
	                throw new AssertionError("Failed to create image view: " + VKUtil.translateVulkanResult(err));
	            }
	        }
	        colorAttachmentView.free();
	        memFree(pBufferView);
	        memFree(pSwapchainImages);

	        Swapchain ret = new Swapchain();
	        ret.images = images;
	        ret.imageViews = imageViews;
	        ret.swapchainHandle = swapChain;
	        return ret;
	    }
	 
	 private void createImageBarrier(VkCommandBuffer cmdbuffer, long image, int aspectMask, int oldImageLayout, int srcAccess, int newImageLayout, int dstAccess) {
		// Create an image barrier object
		VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.calloc(1)
		        .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
		        .pNext(0)
		        .oldLayout(oldImageLayout)
		        .srcAccessMask(srcAccess)
		        .newLayout(newImageLayout)
		        .dstAccessMask(dstAccess)
		        .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
		        .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
		        .image(image);
		imageMemoryBarrier.subresourceRange()
		        .aspectMask(aspectMask)
		        .baseMipLevel(0)
		        .levelCount(1)
		        .layerCount(1);
		
		// Put barrier on top
		int srcStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
		int destStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
		
		// Put barrier inside setup command buffer
		vkCmdPipelineBarrier(cmdbuffer, srcStageFlags, destStageFlags, 0,
		        null, // no memory barriers
		        null, // no buffer memory barriers
		        imageMemoryBarrier); // one image memory barrier
		    imageMemoryBarrier.free();
	}
	 
	private VkImageMemoryBarrier.Buffer createPrePresentBarrier(long presentImage) {
        VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.calloc(1)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .pNext(0)
                .srcAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                .dstAccessMask(0)
                .oldLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .newLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
        imageMemoryBarrier.subresourceRange()
                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                .baseMipLevel(0)
                .levelCount(1)
                .baseArrayLayer(0)
                .layerCount(1);
        imageMemoryBarrier.image(presentImage);
        return imageMemoryBarrier;
    }
	 
	private static void submitCommandBuffer(VkQueue queue, VkCommandBuffer commandBuffer) {
        if (commandBuffer == null || commandBuffer.address() == 0)
            return;
        VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
        PointerBuffer pCommandBuffers = memAllocPointer(1)
                .put(commandBuffer)
                .flip();
        submitInfo.pCommandBuffers(pCommandBuffers);
        int err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
        memFree(pCommandBuffers);
        submitInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to submit command buffer: " + VKUtil.translateVulkanResult(err));
        }
    }
	
	private VkCommandBuffer[] createRenderCommandBuffers(VkDevice device, long commandPool, long[] framebuffers, long renderPass, int width, int height,
            long pipeline, long verticesBuf) {
        // Create the render command buffers (one command buffer per framebuffer image)
        VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(framebuffers.length);
        PointerBuffer pCommandBuffer = memAllocPointer(framebuffers.length);
        int err = vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to allocate render command buffer: " + VKUtil.translateVulkanResult(err));
        }
        VkCommandBuffer[] renderCommandBuffers = new VkCommandBuffer[framebuffers.length];
        for (int i = 0; i < framebuffers.length; i++) {
            renderCommandBuffers[i] = new VkCommandBuffer(pCommandBuffer.get(i), device);
        }
        memFree(pCommandBuffer);
        cmdBufAllocateInfo.free();

        // Create the command buffer begin structure
        VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .pNext(0);

        // Specify clear color (cornflower blue)
        VkClearValue.Buffer clearValues = VkClearValue.calloc(1);
        clearValues.color()
                .float32(0, 100/255.0f)
                .float32(1, 149/255.0f)
                .float32(2, 237/255.0f)
                .float32(3, 1.0f);

        // Specify everything to begin a render pass
        VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .pNext(0)
                .renderPass(renderPass)
                .pClearValues(clearValues);
        VkRect2D renderArea = renderPassBeginInfo.renderArea();
        renderArea.offset().set(0, 0);
        renderArea.extent().set(width, height);

        for (int i = 0; i < renderCommandBuffers.length; ++i) {
            // Set target frame buffer
            renderPassBeginInfo.framebuffer(framebuffers[i]);

            err = vkBeginCommandBuffer(renderCommandBuffers[i], cmdBufInfo);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to begin render command buffer: " + VKUtil.translateVulkanResult(err));
            }

            vkCmdBeginRenderPass(renderCommandBuffers[i], renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);

            // Update dynamic viewport state
            VkViewport.Buffer viewport = VkViewport.calloc(1)
                    .height(height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(1.0f);
            vkCmdSetViewport(renderCommandBuffers[i], 0, viewport);
            viewport.free();

            // Update dynamic scissor state
            VkRect2D.Buffer scissor = VkRect2D.calloc(1);
            scissor.extent().set(width, height);
            scissor.offset().set(0, 0);
            vkCmdSetScissor(renderCommandBuffers[i], 0, scissor);
            scissor.free();

            // Bind the rendering pipeline (including the shaders)
            vkCmdBindPipeline(renderCommandBuffers[i], VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);

            // Bind triangle vertices
            LongBuffer offsets = memAllocLong(1);
            offsets.put(0, 0L);
            LongBuffer pBuffers = memAllocLong(1);
            pBuffers.put(0, verticesBuf);
            vkCmdBindVertexBuffers(renderCommandBuffers[i], 0, pBuffers, offsets);
            memFree(pBuffers);
            memFree(offsets);

            // Draw triangle
            vkCmdDraw(renderCommandBuffers[i], 3, 1, 0, 0);

            vkCmdEndRenderPass(renderCommandBuffers[i]);

            // Add a present memory barrier to the end of the command buffer
            // This will transform the frame buffer color attachment to a
            // new layout for presenting it to the windowing system integration 
            VkImageMemoryBarrier.Buffer prePresentBarrier = createPrePresentBarrier(swapchain.images[i]);
            vkCmdPipelineBarrier(renderCommandBuffers[i],
                VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                0,
                null, // No memory barriers
                null, // No buffer memory barriers
                prePresentBarrier); // One image memory barrier
            prePresentBarrier.free();

            err = vkEndCommandBuffer(renderCommandBuffers[i]);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to begin render command buffer: " + VKUtil.translateVulkanResult(err));
            }
        }
        renderPassBeginInfo.free();
        clearValues.free();
        cmdBufInfo.free();
        return renderCommandBuffers;
    }
	
	private long[] createFramebuffers(VkDevice device, Swapchain swapchain, long renderPass, int width, int height) {
        LongBuffer attachments = memAllocLong(1);
        VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(attachments)
                .flags(0)
                .height(height)
                .width(width)
                .layers(1)
                .pNext(0)
                .renderPass(renderPass);
        // Create a framebuffer for each swapchain image
        long[] framebuffers = new long[swapchain.images.length];
        LongBuffer pFramebuffer = memAllocLong(1);
        for (int i = 0; i < swapchain.images.length; i++) {
            attachments.put(0, swapchain.imageViews[i]);
            int err = vkCreateFramebuffer(device, fci, null, pFramebuffer);
            long framebuffer = pFramebuffer.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + VKUtil.translateVulkanResult(err));
            }
            framebuffers[i] = framebuffer;
        }
        memFree(attachments);
        memFree(pFramebuffer);
        fci.free();
        return framebuffers;
    }
	 
	private VkPipelineShaderStageCreateInfo loadShader(VkDevice device, String classPath, int stage) throws IOException {
        VkPipelineShaderStageCreateInfo shaderStage = VkPipelineShaderStageCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(stage)
                .module(loadShader(classPath, device))
                .pName(memUTF8("main"));
        return shaderStage;
    }
	 
	private static long loadShader(String filePath, VkDevice device) throws IOException {
	    ByteBuffer shaderCode = ResourceLoader.ioResourceToByteBuffer(filePath, 1024);
	    int err;
	    VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc()
	            .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
	            .pNext(0)
	            .pCode(shaderCode)
	            .flags(0);
	    LongBuffer pShaderModule = memAllocLong(1);
	    err = vkCreateShaderModule(device, moduleCreateInfo, null, pShaderModule);
	    long shaderModule = pShaderModule.get(0);
	    memFree(pShaderModule);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create shader module: " + VKUtil.translateVulkanResult(err));
	    }
	    return shaderModule;
	}
	 
	private boolean getMemoryType(VkPhysicalDeviceMemoryProperties deviceMemoryProperties, int typeBits, int properties, IntBuffer typeIndex) {
	    int bits = typeBits;
	    for (int i = 0; i < 32; i++) {
	        if ((bits & 1) == 1) {
	            if ((deviceMemoryProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
	                typeIndex.put(0, i);
	                return true;
	            }
	        }
	        bits >>= 1;
	    }
	    return false;
	}

	@Override
	public boolean isGrid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCameraUnderWater() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterReflection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterRefraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBloomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Framebuffer getMultisampledFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Framebuffer getDeferredFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Texture getSceneDepthmap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quaternion getClipplane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getUnderwater() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClipplane(Quaternion plane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGrid(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterRefraction(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterReflection(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCameraUnderWater(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSightRangeFactor(float range) {
		// TODO Auto-generated method stub
		
	}

}
