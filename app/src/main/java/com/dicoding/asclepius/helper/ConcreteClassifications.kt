import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.classifier.Classifications

class ConcreteClassifications(categories: List<Category>, headIndex: Int) : Classifications() {
    private val categories: List<Category> = categories
    private val headIndex: Int = headIndex

    override fun getCategories(): List<Category> {
        return categories
    }

    override fun getHeadIndex(): Int {
        return headIndex
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (category in categories) {
            val scorePercentage = "${(category.score * 100).toInt()}%"
            sb.append("${category.label}: $scorePercentage\n")
        }
        return sb.toString()
    }
}