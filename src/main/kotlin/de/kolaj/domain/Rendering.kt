package de.kolaj.domain

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO


// how the user creates a custom render pipeline
data class CanvasSpecification(val width: Int, val height: Int)

interface RenderStepSpecification {

    fun getRenderStepType(): RenderStepType
}

interface ScalingSpecification

interface DistributionSpecification {
    fun getDistributionType(): DistributionType
}

data class RenderPlan(
    val id: Long,
    val canvasSpecification: CanvasSpecification,
    val renderStepSpecifications: List<RenderStepSpecification>,
    val scalingSpecifications: List<ScalingSpecification>,
    val distributionSpecifications: List<DistributionSpecification>
)


// the info needed for creating a specific rendering
interface RenderStepInfo

interface ScalingInfo

interface DitributionInfo

data class RenderJob(val id: Long, val renderStepInfos: List<RenderStepInfo>)


// the renderer that creates a pipeline as specified by RenderPlan to create the rendering specified by RenderJob

class Canvas(val image: BufferedImage) {

    companion object {
        fun create(canvasSpecification: CanvasSpecification): Canvas {
            val image = BufferedImage(canvasSpecification.width, canvasSpecification.height, TYPE_INT_RGB)
            val graphics2d: Graphics2D = image.createGraphics()
            graphics2d.setRenderingHints(
                mapOf(
                    RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                    RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB,
                    RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY,
                    RenderingHints.KEY_INTERPOLATION to RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
                    RenderingHints.KEY_FRACTIONALMETRICS to RenderingHints.VALUE_FRACTIONALMETRICS_ON
                )
            )
            return Canvas(image)
        }
    }

    fun getGraphics(): Graphics2D {
        return image.createGraphics()
    }
}

enum class RenderStepType {
    PAINT,
    TEXT,
    COLOR_FILL
}

interface RenderStep {

    fun doRenderStep(canvas: Graphics2D) // add RenderStepInfo
}

class TextRenderer() : RenderStep {
    override fun doRenderStep(canvas: Graphics2D) {
        canvas.drawString("Hello world", 0, 0)
    }

}

class RenderPipeline(val renderSteps: List<RenderStep>) {

    companion object {

        fun create(renderPlan: RenderPlan): RenderPipeline {
            return RenderPipeline(listOf(TextRenderer()))
        }
    }

    fun render(canvas: Canvas): Canvas {
        for (renderStep in renderSteps) {
            renderStep.doRenderStep(canvas.getGraphics())
        }
        return canvas
    }
}

class Scaler() {

    companion object {
        fun create(scalingSpecification: ScalingSpecification): Scaler {
            return Scaler()
        }
    }
}

enum class DistributionType {
    FILE_SYSTEM
}

interface Distributor {

    fun distribute(canvas: Canvas)

    companion object {
        fun create(distributionSpecification: DistributionSpecification): Distributor {
            return FileSystemDistributor("C://data//test.jpg")
        }
    }
}

class FileSystemDistributor(val path: String) : Distributor {
    override fun distribute(canvas: Canvas) {
        ImageIO.write(canvas.image, "png", File(path))
    }

}

class Renderer {

    fun render(renderJob: RenderJob, renderPlan: RenderPlan) {
        val canvas = Canvas.create(renderPlan.canvasSpecification)
    }
}

