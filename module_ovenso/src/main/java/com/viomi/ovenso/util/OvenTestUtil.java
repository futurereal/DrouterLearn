package com.viomi.ovenso.util;

import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.spec.OvenPropEnum;

/**
 * @description:
 * @data:2022/1/26
 */
public class OvenTestUtil {

    /**
     * 模拟启动完成的逻辑
     */
    public static void testCookingUI(OvenWorkStatusEnum ovenWorkStatusEnum) {
        if (OvenConstants.IS_TEST_UI) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_COOK_STATUSCHANGE, ovenWorkStatusEnum.value);
        }
    }

    public static void testPropertySuccess() {
        if (OvenConstants.IS_TEST_UI) {
            ViomiRxBus.getInstance().post(CommonConstant.MSG_DOWNWRITE_SUCCESS);
        }
    }

    public static void testActionSuccess() {
        if (OvenConstants.IS_TEST_UI) {
            ViomiRxBus.getInstance().post(CommonConstant.MSG_ACTION_SUCCESS);
        }
    }

    static int index = 4;

    public static void testChugou() {
        if (OvenConstants.IS_TEST_UI) {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setSid(OvenPropEnum.MODE_STEP.siid);
            propertyEntity.setPid(OvenPropEnum.MODE_STEP.piid);
            int currentIndex = index % 3 + 1;
            propertyEntity.setContent(currentIndex);
            index++;
            OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
        }
    }

    public static void testRecipeStep() {
        if (OvenConstants.IS_TEST_UI) {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setSid(OvenPropEnum.RECIPE_STEP.siid);
            propertyEntity.setPid(OvenPropEnum.RECIPE_STEP.piid);
            int currentIndex = index % 3 + 1;
            propertyEntity.setContent(currentIndex);
            index++;
            OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
        }
    }

    public static void testWaterTankError() {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setSid(OvenPropEnum.WATERTANK_ISCLOSE.siid);
        propertyEntity.setPid(OvenPropEnum.WATERTANK_ISCLOSE.piid);
        propertyEntity.setContent(true);
        OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
    }

    public static void testModeStep() {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setSid(OvenPropEnum.MODE_STEP.siid);
        propertyEntity.setPid(OvenPropEnum.MODE_STEP.piid);
        propertyEntity.setContent(index);
        index++;
        OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
    }
}
