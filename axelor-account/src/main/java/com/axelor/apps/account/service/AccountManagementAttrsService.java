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
package com.axelor.apps.account.service;

import com.axelor.apps.account.db.AccountManagement;
import com.axelor.apps.account.db.repo.AccountManagementRepository;
import com.axelor.apps.base.db.ProductFamily;
import com.axelor.common.ObjectUtils;
import com.axelor.inject.Beans;
import com.axelor.utils.helpers.StringHelper;
import java.util.List;
import java.util.stream.Collectors;

public class AccountManagementAttrsService {

  public String getCompanyDomain(AccountManagement accountManagement) {
    return getCompanyDomain(accountManagement, accountManagement.getProductFamily());
  }

  public String getCompanyDomain(AccountManagement accountManagement, ProductFamily productFamily) {
    String domain = "(self.archived IS NULL OR self.archived = false)";

    if (productFamily != null) {
      List<AccountManagement> existingAccountManagements =
          Beans.get(AccountManagementRepository.class)
              .all()
              .filter("self.productFamily = :productFamily")
              .bind("productFamily", productFamily)
              .fetch();

      if (!ObjectUtils.isEmpty(existingAccountManagements)) {
        domain +=
            String.format(
                " AND self.id NOT IN (%s)",
                StringHelper.getIdListString(
                    existingAccountManagements.stream()
                        .map(AccountManagement::getCompany)
                        .collect(Collectors.toList())));
      }
    }
    return domain;
  }
}
