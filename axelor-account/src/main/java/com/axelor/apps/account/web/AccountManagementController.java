/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.web;

import com.axelor.apps.account.db.AccountManagement;
import com.axelor.apps.account.service.AccountManagementAttrsService;
import com.axelor.apps.account.service.analytic.AnalyticAttrsService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.ProductFamily;
import com.axelor.apps.base.db.repo.ProductFamilyRepository;
import com.axelor.apps.base.service.exception.ErrorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Singleton;
import java.util.Map;

@Singleton
public class AccountManagementController {

  @ErrorException
  public void setDomainAnalyticDistributionTemplate(ActionRequest request, ActionResponse response)
      throws AxelorException {
    Context context = request.getContext();
    AccountManagement accountManagement = context.asType(AccountManagement.class);

    response.setAttr(
        "analyticDistributionTemplate",
        "domain",
        Beans.get(AnalyticAttrsService.class)
            .getAnalyticDistributionTemplateDomain(
                null,
                accountManagement.getProduct(),
                accountManagement.getCompany(),
                null,
                null,
                false));
  }

  @ErrorException
  public void setCompanyDomain(ActionRequest request, ActionResponse response)
      throws AxelorException {
    AccountManagement accountManagement = request.getContext().asType(AccountManagement.class);
    ProductFamily productFamily = accountManagement.getProductFamily();

    if (productFamily == null) {
      @SuppressWarnings("unchecked")
      Map<String, Object> parentContext = (Map<String, Object>) request.getContext().get("_parent");
      if (parentContext != null
          && "com.axelor.apps.base.db.ProductFamily".equals(parentContext.get("_model"))) {
        Object idObj = parentContext.get("id");
        if (idObj != null) {
          Long productFamilyId;
          if (idObj instanceof Number) {
            productFamilyId = ((Number) idObj).longValue();
          } else {
            productFamilyId = Long.valueOf(idObj.toString());
          }
          productFamily = Beans.get(ProductFamilyRepository.class).find(productFamilyId);
        }
      }
    }

    String domain =
        Beans.get(AccountManagementAttrsService.class)
            .getCompanyDomain(accountManagement, productFamily);

    response.setAttr("company", "domain", domain);
  }
}
