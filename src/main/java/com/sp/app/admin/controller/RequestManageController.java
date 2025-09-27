package com.sp.app.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.admin.service.RequestListService;
import com.sp.app.entity.Material;
import com.sp.app.entity.MaterialReward;
import com.sp.app.entity.Request;
import com.sp.app.repository.MaterialRepository;
import com.sp.app.repository.MaterialRewardRepository;
import com.sp.app.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/request")
public class RequestManageController {

    private final RequestRepository requestRepository;
    private final RequestListService requestListService;   
    private final MaterialRepository materialRepository;
    private final MaterialRewardRepository materialRewardRepository;

    @GetMapping("/requestList")
    public String list(
            @RequestParam(name = "schType", defaultValue = "all") String schType, 
            @RequestParam(name = "kwd", required = false) String kwd,
            @RequestParam(name = "level", required = false) Integer levelParam,  
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        Integer level = levelParam;
        String text;

        if ("level".equalsIgnoreCase(schType)) {
            if (level == null && kwd != null) {
                String digits = kwd.replaceAll("\\D+", ""); 
                if (!digits.isEmpty()) level = Integer.valueOf(digits);
            }
            text = ""; 
        } else {
            text = (kwd == null) ? "" : kwd.trim();
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Request> result = requestRepository.search(text, level, pageable);

        model.addAttribute("page", result);
        model.addAttribute("kwd", kwd);
        model.addAttribute("level", level);
        model.addAttribute("schType", schType);
        return "admin/request/requestList";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Request req = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의뢰가 존재하지 않습니다: " + id));
        
        List<MaterialReward> rewardItems = materialRewardRepository.findByRequest_RequestId(id);
        
        model.addAttribute("req", req);
        model.addAttribute("rewardItems", rewardItems);
        
        return "admin/request/requestDetail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("req", new Request());
        return "admin/request/requestWrite";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        boolean inProgress = requestListService.hasAnyInProgressUser(id);
        if (inProgress) {
            ra.addFlashAttribute("error", "진행중인 사용자가 있어 삭제할 수 없습니다.");
            return "redirect:/admin/request/requestList";
        }
        requestRepository.deleteById(id);
        ra.addFlashAttribute("msg", "의뢰를 삭제했습니다.");
        	return "redirect:/admin/request/requestList";
    }
    
    @GetMapping("/requestWrite")
    public String requestWrite(Model model) {
    	model.addAttribute("mode", "write");
        model.addAttribute("req", new Request()); 
        model.addAttribute("materials", materialRepository.findAll()); 
        return "admin/request/requestWrite";
    }
    
    @PostMapping("/write")
    @Transactional
    public String write(
            @ModelAttribute Request req, 
            @RequestParam(name = "rewardMaterialId", required = false) java.util.List<Long> rewardMaterialIds,
            @RequestParam(name = "rewardQty", required = false) java.util.List<Integer> rewardQtys,
            RedirectAttributes ra) {

        req.setRewardEnable(1); 
        req.setRequestDate(java.time.LocalDateTime.now());
        Request saved = requestRepository.save(req);

        if (rewardMaterialIds != null && rewardQtys != null) {
            int n = Math.min(rewardMaterialIds.size(), rewardQtys.size());
            java.util.Map<Long, Integer> merged = new java.util.HashMap<>();
            for (int i = 0; i < n; i++) {
                Long mid = rewardMaterialIds.get(i);
                Integer q  = rewardQtys.get(i);
                if (mid == null || q == null || q <= 0) continue;
                merged.merge(mid, q, Integer::sum); 
            }
            for (var e : merged.entrySet()) {
                MaterialReward mr = new MaterialReward();
                mr.setRequest(saved);
                mr.setMaterial(materialRepository.getReferenceById(e.getKey()));
                mr.setQty(e.getValue());
                materialRewardRepository.save(mr);
            }
        }

        ra.addFlashAttribute("msg", "의뢰가 등록되었습니다.");
        return "redirect:/admin/request/requestList";
    }
    
    @GetMapping("/material/search")
    @ResponseBody
    public List<Map<String, Object>> searchMaterials(
            @RequestParam(name = "q", required = false, defaultValue = "") String q) {

        String keyword = (q == null) ? "" : q.trim();
        List<Material> list = materialRepository.searchByName(keyword); 

        List<Map<String, Object>> result = new ArrayList<>();
        for (Material m : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("id",    m.getMaterialId());
            row.put("level", m.getMaterialLevel());
            row.put("name",  m.getMaterialName());
            row.put("photo", m.getMaterialPhoto());
            result.add(row);
        }
        return result;
    }

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable("id") Long id,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             Model model) {
        Request req = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의뢰가 존재하지 않습니다: " + id));

        List<MaterialReward> rewardItems = materialRewardRepository.findByRequest_RequestId(id);

        List<Material> materials = materialRepository.findAll();

        model.addAttribute("mode", "update"); 
        model.addAttribute("req", req);
        model.addAttribute("rewardItems", rewardItems);
        model.addAttribute("materials", materials);
        model.addAttribute("page", page);

        return "admin/request/requestWrite";  
    }

    @PostMapping("/{id}/update")
    @Transactional
    public String update(@PathVariable("id") Long id,
                         Request form, 
                         @RequestParam(name = "rewardMaterialId", required = false) List<Long> rewardMaterialIds,
                         @RequestParam(name = "rewardQty", required = false) List<Integer> rewardQtys,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         RedirectAttributes ra) {

        Request origin = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의뢰가 존재하지 않습니다: " + id));

        origin.setRequestName(form.getRequestName());
        origin.setRequestContent(form.getRequestContent());
        origin.setRequestLevel(form.getRequestLevel());
        origin.setClient(form.getClient());
        origin.setGoalCount(form.getGoalCount());
        origin.setDeadline(form.getDeadline());
        origin.setRewardExp(form.getRewardExp());
        origin.setRewardGold(form.getRewardGold());
        origin.setRewardEnable(1);

        List<MaterialReward> old = materialRewardRepository.findByRequest_RequestId(id);
        materialRewardRepository.deleteAll(old);

        if (rewardMaterialIds != null && rewardQtys != null) {
            int n = Math.min(rewardMaterialIds.size(), rewardQtys.size());
            java.util.Map<Long, Integer> merged = new java.util.HashMap<>();
            for (int i = 0; i < n; i++) {
                Long mid = rewardMaterialIds.get(i);
                Integer qty = rewardQtys.get(i);
                if (mid == null || qty == null || qty <= 0) continue;
                merged.merge(mid, qty, Integer::sum);
            }
            for (var e : merged.entrySet()) {
                MaterialReward mr = new MaterialReward();
                mr.setRequest(origin);
                mr.setMaterial(materialRepository.getReferenceById(e.getKey()));
                mr.setQty(e.getValue());
                materialRewardRepository.save(mr);
            }
        }

        requestRepository.save(origin);

        ra.addFlashAttribute("msg", "의뢰가 수정되었습니다.");
        return "redirect:/admin/request/" + id + "?page=" + page;
    }


}
