import com.example.server.DAO.FixedAssetDAO;
import com.example.server.Models.Entities.FixedAsset;
import com.example.server.Services.FixedAssetService;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FixedAssetServiceTest {

    private FixedAssetService fixedAssetService;
    private FixedAssetDAO fixedAssetDAO;

    @BeforeEach
    void setUp() {
        fixedAssetDAO = mock(FixedAssetDAO.class);
        fixedAssetService = new FixedAssetService(fixedAssetDAO);
    }

    @Test
    void testInsertFixedAsset() {
        FixedAsset asset = new FixedAsset();
        asset.setName("Laptop");
        asset.setCategory("Electronics");

        fixedAssetService.insert(asset);


        verify(fixedAssetDAO, times(1)).insert(asset);
    }

    @Test
    void testUpdateFixedAsset() {
        FixedAsset asset = new FixedAsset();
        asset.setId(1);
        asset.setName("Updated Laptop");

        fixedAssetService.update(asset);

        verify(fixedAssetDAO, times(1)).update(asset);
    }

    @Test
    void testDeleteFixedAsset() {
        int assetId = 1;

        fixedAssetService.delete(assetId);

        verify(fixedAssetDAO, times(1)).delete(assetId);
    }

    @Test
    void testFindById() {
        int assetId = 1;
        FixedAsset asset = new FixedAsset();
        asset.setId(assetId);
        asset.setName("Test Asset");

        when(fixedAssetDAO.find(assetId)).thenReturn(asset);

        // Действие
        FixedAsset result = fixedAssetService.findById(assetId);

        // Проверка
        assertNotNull(result);
        assertEquals(assetId, result.getId());
        assertEquals("Test Asset", result.getName());
        verify(fixedAssetDAO, times(1)).find(assetId);
    }

    @Test
    void testFindAll() {
        FixedAsset asset1 = new FixedAsset();
        asset1.setName("Asset 1");
        FixedAsset asset2 = new FixedAsset();
        asset2.setName("Asset 2");

        when(fixedAssetDAO.findAll()).thenReturn(List.of(asset1, asset2));

        // Действие
        List<FixedAsset> assets = fixedAssetService.findAll();

        // Проверка
        assertNotNull(assets);
        assertEquals(2, assets.size());
        verify(fixedAssetDAO, times(1)).findAll();
    }

    @Test
    void testSearchAssets() {
        String parameter = "название";
        String value = "Laptop";

        FixedAsset asset = new FixedAsset();
        asset.setName("Laptop");

        when(fixedAssetDAO.searchAssets(parameter, value)).thenReturn(List.of(asset));

        // Действие
        List<FixedAsset> result = fixedAssetService.searchAssets(parameter, value);

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(fixedAssetDAO, times(1)).searchAssets(parameter, value);
    }

    @Test
    void testSortAssets() {
        String sortBy = "категория";
        String sortOrder = "ASC";

        FixedAsset asset1 = new FixedAsset();
        asset1.setCategory("A");
        FixedAsset asset2 = new FixedAsset();
        asset2.setCategory("B");

        when(fixedAssetDAO.sortAssets(sortBy, sortOrder)).thenReturn(List.of(asset1, asset2));

        // Действие
        List<FixedAsset> result = fixedAssetService.sortAssets(sortBy, sortOrder);

        // Проверка
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getCategory());
        assertEquals("B", result.get(1).getCategory());
        verify(fixedAssetDAO, times(1)).sortAssets(sortBy, sortOrder);
    }
}
