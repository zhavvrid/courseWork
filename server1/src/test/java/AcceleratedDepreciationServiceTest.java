import com.example.server.Models.Entities.AcceleratedDepreciation;
import com.example.server.Models.Entities.DecliningBalanceDepreciation;
import com.example.server.Models.Entities.FixedAsset;
import com.example.server.Models.Entities.StraightLineDepreciation;
import com.example.server.Services.FixedAssetService;
import com.example.server.Services.DepreciationCalculationService;
import com.example.server.Utility.ClientThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepreciationCalculationTests {

    private FixedAssetService fixedAssetService;
    private DepreciationCalculationService calculationService;
    private ClientThread clientThread;

    @BeforeEach
    void setUp() {
        fixedAssetService = mock(FixedAssetService.class);
        calculationService = mock(DepreciationCalculationService.class);
        clientThread = new ClientThread(fixedAssetService, calculationService);
    }

    private FixedAsset createFixedAsset(int id, BigDecimal initialCost, BigDecimal residualValue, int usefulLife, String purchaseDate) {
        FixedAsset asset = new FixedAsset();
        asset.setId(id);
        asset.setInitialCost(initialCost);
        asset.setResidualValue(residualValue);
        asset.setUsefulLife(usefulLife);
        asset.setPurchaseDate(purchaseDate);
        return asset;
    }

    @Test
    void testCalculateStraightLineDepreciationReturnsValidResults() {
        FixedAsset asset = createFixedAsset(1, BigDecimal.valueOf(10000), BigDecimal.valueOf(1000), 5, "2020-01-01");
        when(fixedAssetService.findById(1)).thenReturn(asset);

        List<StraightLineDepreciation> results = clientThread.calculateStraightLineDepreciation(1);

        assertNotNull(results);
        assertEquals(5, results.size());

        BigDecimal annualDepreciation = BigDecimal.valueOf(1800);
        for (StraightLineDepreciation depreciation : results) {
            assertEquals(annualDepreciation, depreciation.getDepreciationAmount());
        }
        verify(fixedAssetService, times(1)).findById(1);
    }


}
