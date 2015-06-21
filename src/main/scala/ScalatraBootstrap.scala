import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.DefaultRandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent, ReferenceSetFileLoaderComponent}
import com.clackjones.connectivitymap.rest.{ExperimentController, MainController, QuerySignatureController}
import com.clackjones.connectivitymap.service._
import org.scalatra.LifeCycle
import javax.servlet.ServletContext


class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val experimentController = new ExperimentController with InMemoryExperimentProviderComponent
      with DefaultExperimentRunnerComponent with DefaultRandomSignatureGeneratorComponent
      with ReferenceSetFileLoaderComponent with ReferenceProfileFileLoaderComponent
      with FileBasedQuerySignatureProviderComponent with InMemoryExperimentResultProviderComponent
      with ConnectivityMapModule with FileBasedReferenceSetProviderComponent
      with ReferenceSetCreatorByDrugDoseAndCellLineComponent

    // mount servlets like this:
    context mount (new MainController, "/")
    context mount (new QuerySignatureController with FileBasedQuerySignatureProviderComponent, "/querysignature/*")
    context mount (experimentController, "/experiment/*")
  }
}
