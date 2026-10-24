// ── Static resource tables ────────────────────────────────────────────────────
const TECH_RESOURCES = {
  fundamentals: ['MDN Web Docs — https://developer.mozilla.org', 'freeCodeCamp — https://www.freecodecamp.org'],
  tooling:      ['VS Code Docs — https://code.visualstudio.com/docs', 'Git Handbook — https://guides.github.com/introduction/git-handbook'],
  architecture: ['Refactoring Guru — https://refactoring.guru/design-patterns', 'roadmap.sh — https://roadmap.sh'],
  api:          ['REST API Tutorial — https://restfulapi.net', 'Postman Learning — https://learning.postman.com'],
  advanced:     ['web.dev Performance — https://web.dev/performance', 'JavaScript.info — https://javascript.info'],
  testing:      ['Jest Docs — https://jestjs.io/docs/getting-started', 'Testing Library — https://testing-library.com/docs'],
  capstone:     ['GitHub — https://github.com', 'Vercel (Deploy) — https://vercel.com']
};

const ART_RESOURCES = {
  foundations:  ['Skillshare — https://www.skillshare.com', 'YouTube Art Tutorials — https://www.youtube.com/results?search_query=art+for+beginners'],
  precision:    ['Proko Drawing — https://www.proko.com', 'Pinterest References — https://www.pinterest.com'],
  composition:  ['Ctrl+Paint — https://www.ctrlpaint.com', 'Udemy Art Courses — https://www.udemy.com/courses/design-and-creativity'],
  speed:        ['Domestika — https://www.domestika.org', 'ArtStation — https://www.artstation.com'],
  portfolio:    ['Behance — https://www.behance.net', 'Instagram Tips — https://www.instagram.com']
};

// ── Smart fallback generator ──────────────────────────────────────────────────
function generateSmartRoadmap(currentSkill, goal, experience) {
    const isArt = /design|art|mehandi|drawing|painting|music|cooking|craft|photo|video|yoga|fashion|dance|beauty/i.test(goal + ' ' + currentSkill);

    let steps;
    if (isArt) {
        steps = [
            {
                title: `Foundations & Essential Materials for ${currentSkill}`,
                description: `Understand basic techniques, material preparation, and sketching principles for ${goal}. Study reference images and gather your tools.`,
                estimatedTime: '1–2 weeks',
                miniTask: `Spend 20 minutes daily sketching basic shapes and patterns related to ${goal}.`,
                youtubeSearch: `${goal} beginner tutorial`,
                resources: ART_RESOURCES.foundations,
                completed: false
            },
            {
                title: 'Mastering Precision, Lines & Symmetry',
                description: 'Practice core hand movements, intricate patterns, and structural layout techniques. Aim for 15–20 minutes of daily focused drills.',
                estimatedTime: '2–3 weeks',
                miniTask: 'Complete 5 symmetry exercises — draw a shape on one side and mirror it freehand.',
                youtubeSearch: `${goal} precision lines practice`,
                resources: ART_RESOURCES.precision,
                completed: false
            },
            {
                title: 'Advanced Composition & Creative Styles',
                description: `Explore contemporary styles, shading, depth, and unique artistic variations suited to ${goal}. Study masters in the field.`,
                estimatedTime: '3–4 weeks',
                miniTask: `Recreate 3 compositions you admire in your own style, then add a personal twist.`,
                youtubeSearch: `${goal} composition and style`,
                resources: ART_RESOURCES.composition,
                completed: false
            },
            {
                title: 'Speed, Flow & Practical Execution',
                description: 'Build muscle memory, work efficiently under time constraints, and refine your technique across varied subjects.',
                estimatedTime: '2–3 weeks',
                miniTask: 'Do timed practice: complete a full piece in 15 minutes, then 10, then 5.',
                youtubeSearch: `${goal} speed practice tips`,
                resources: ART_RESOURCES.speed,
                completed: false
            },
            {
                title: 'Portfolio & Masterpiece Project',
                description: `Create a professional portfolio or showcase piece demonstrating your ${goal} mastery. Document progress for clients or social media.`,
                estimatedTime: '1–2 weeks',
                miniTask: 'Create 5 portfolio pieces, photograph them professionally, and post on Behance or Instagram.',
                youtubeSearch: `${goal} portfolio tips`,
                resources: ART_RESOURCES.portfolio,
                completed: false
            }
        ];
    } else {
        steps = [
            {
                title: `${currentSkill} Refresher & Core Fundamentals`,
                description: `Review foundational concepts of ${currentSkill} — syntax, data structures, and best practices for a ${experience} learner heading toward ${goal}.`,
                estimatedTime: '1–2 weeks',
                miniTask: `Build a small ${currentSkill} program that reads input, processes data, and outputs results.`,
                youtubeSearch: `${currentSkill} fundamentals crash course`,
                resources: TECH_RESOURCES.fundamentals,
                completed: false
            },
            {
                title: 'Development Environment & Tooling Setup',
                description: `Configure your workspace, install CLI tools, linters, and all dependencies required for ${goal}. Version control with Git from day one.`,
                estimatedTime: '3–5 days',
                miniTask: 'Set up VS Code with extensions, create a GitHub repo, make your first commit and push.',
                youtubeSearch: `${goal} development environment setup`,
                resources: TECH_RESOURCES.tooling,
                completed: false
            },
            {
                title: 'Core Architecture & Intermediate Patterns',
                description: `Master data flow, state management, component design, and structural patterns directly relevant to ${goal}. Build small focused projects.`,
                estimatedTime: '2–3 weeks',
                miniTask: `Build a mini ${goal} project using at least 3 key architectural patterns you learned.`,
                youtubeSearch: `${goal} architecture patterns tutorial`,
                resources: TECH_RESOURCES.architecture,
                completed: false
            },
            {
                title: 'API Integration & Asynchronous Workflows',
                description: 'Learn to fetch data from REST APIs, handle network errors gracefully, and manage async state — a must-have skill for any modern application.',
                estimatedTime: '1–2 weeks',
                miniTask: 'Connect a free public API (weather/news/jokes), display data in a UI, and handle errors gracefully.',
                youtubeSearch: `${goal} REST API integration tutorial`,
                resources: TECH_RESOURCES.api,
                completed: false
            },
            {
                title: 'Advanced Techniques & Optimization',
                description: `Explore performance tuning, lazy loading, caching, and robust error handling patterns used by professional ${goal} developers.`,
                estimatedTime: '2 weeks',
                miniTask: `Profile your existing project, identify one bottleneck, and optimize it with a measurable improvement.`,
                youtubeSearch: `${goal} performance optimization tips`,
                resources: TECH_RESOURCES.advanced,
                completed: false
            },
            {
                title: 'Testing, Debugging & Quality Assurance',
                description: 'Write unit and integration tests. Debug using DevTools. Target ≥70% test coverage on your next project.',
                estimatedTime: '1 week',
                miniTask: 'Add unit tests to your existing project — cover at least 5 core functions.',
                youtubeSearch: `${goal} unit testing tutorial`,
                resources: TECH_RESOURCES.testing,
                completed: false
            },
            {
                title: `Capstone Project — Build a Real-World ${goal} App`,
                description: `Apply everything by planning, designing, building, and deploying a complete ${goal} project. Push to GitHub and share it.`,
                estimatedTime: '2–4 weeks',
                miniTask: 'Define your capstone idea in one sentence, create a GitHub repo, and commit your first working feature today.',
                youtubeSearch: `${goal} full project tutorial`,
                resources: TECH_RESOURCES.capstone,
                completed: false
            }
        ];
    }

    return {
        title: `Roadmap: ${currentSkill} ➔ ${goal}`,
        description: `Personalized ${experience}-level path from ${currentSkill} to ${goal}. Each step includes estimated time, a mini-task, and curated resources.`,
        steps
    };
}

// ── Gemini AI generator ───────────────────────────────────────────────────────
async function generateGeminiRoadmap(currentSkill, goal, experience, apiKey) {
    const prompt = `Create a highly specialized, practical, and personalized learning roadmap for a ${experience} learner who currently knows "${currentSkill}" and wants to master "${goal}". Tailor it completely to the domain (art/creative vs technical). Return a JSON object with "title", "description", and a "steps" array of 5-7 steps. Each step must have: "title" (string), "description" (string, 2-3 sentences), "estimatedTime" (string like "1-2 weeks"), "miniTask" (string — one concrete small task to practice this step), "youtubeSearch" (string — a YouTube search query for this step), "resources" (array of 2-3 strings in format "Label — URL" pointing to real free resources), "completed": false.`;

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 30000);

    try {
        const response = await fetch(
            'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent',
            {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'x-goog-api-key': apiKey },
                body: JSON.stringify({
                    contents: [{ parts: [{ text: prompt }] }],
                    generationConfig: { responseMimeType: 'application/json' }
                }),
                signal: controller.signal
            }
        );

        if (!response.ok) {
            const errorBody = await response.text();
            let message = `Gemini API returned HTTP ${response.status}`;
            try {
                const p = JSON.parse(errorBody);
                message += p.error?.message ? `: ${p.error.message}` : `: ${errorBody.slice(0, 200)}`;
            } catch { message += `: ${errorBody.slice(0, 200)}`; }
            throw new Error(message);
        }

        const result = await response.json();
        const content = result.candidates?.[0]?.content?.parts?.map(p => p.text || '').join('').trim();
        if (!content) throw new Error('Gemini returned an empty response');

        const roadmap = JSON.parse(content);
        if (
            typeof roadmap.title !== 'string' ||
            typeof roadmap.description !== 'string' ||
            !Array.isArray(roadmap.steps) || roadmap.steps.length === 0 ||
            !roadmap.steps.every(s => s && typeof s.title === 'string' && typeof s.description === 'string')
        ) throw new Error('Gemini returned an invalid roadmap format');

        roadmap.steps = roadmap.steps.map(s => ({
            title: s.title,
            description: s.description,
            estimatedTime: typeof s.estimatedTime === 'string' ? s.estimatedTime : '1-2 weeks',
            miniTask: typeof s.miniTask === 'string' ? s.miniTask : '',
            youtubeSearch: typeof s.youtubeSearch === 'string' ? s.youtubeSearch : `${goal} tutorial`,
            resources: Array.isArray(s.resources) ? s.resources.filter(r => typeof r === 'string') : [],
            completed: false
        }));
        return roadmap;
    } finally {
        clearTimeout(timeout);
    }
}

// ── Route handler ─────────────────────────────────────────────────────────────
exports.generateRoadmap = async (req, res) => {
    try {
        const { currentSkill, goal, experience } = req.body || {};
        if (
            typeof currentSkill !== 'string' || typeof goal !== 'string' ||
            !currentSkill.trim() || !goal.trim() ||
            currentSkill.length > 100 || goal.length > 100
        ) {
            return res.status(400).json({ message: 'Current skill and goal are required and must be 100 characters or fewer' });
        }

        const exp = typeof experience === 'string' && experience.trim() ? experience.trim() : 'Beginner';
        const apiKey = process.env.GEMINI_API_KEY;

        if (apiKey) {
            try {
                return res.json(await generateGeminiRoadmap(currentSkill.trim(), goal.trim(), exp, apiKey));
            } catch (aiErr) {
                console.error('Gemini error, using smart fallback:', aiErr.message);
            }
        }
        return res.json(generateSmartRoadmap(currentSkill.trim(), goal.trim(), exp));
    } catch (err) {
        console.error('Generate roadmap error:', err.message);
        const { currentSkill, goal, experience } = req.body || {};
        const exp = typeof experience === 'string' && experience.trim() ? experience.trim() : 'Beginner';
        return res.json(generateSmartRoadmap(
            typeof currentSkill === 'string' ? currentSkill.trim() : 'Skill',
            typeof goal === 'string' ? goal.trim() : 'Goal',
            exp
        ));
    }
};
