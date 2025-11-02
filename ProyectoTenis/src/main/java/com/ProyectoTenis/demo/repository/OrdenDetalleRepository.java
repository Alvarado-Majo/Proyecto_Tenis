// com.ProyectoTenis.demo.repository.OrdenDetalleRepository
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.domain.Orden;

public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {

    List<OrdenDetalle> findByOrden(Orden orden);

    OrdenDetalle findByOrdenAndTenis(Orden orden, Tenis tenis); // <-- agregar
}
